package com.ths.service

import com.google.common.collect.Multimaps
import com.ths.model.RevisionAndBranch
import com.ths.svn.SvnClient
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.apache.log4j.Logger
import java.util.concurrent.atomic.AtomicInteger

private val log = Logger.getLogger(BranchAndNoOfChanges::class.java)

data class BranchAndNoOfChanges(val branchUrl: String, val author: String, val noOfChanges: Int)
/**
 * NOT THREAD SAFE!
 */
class ChangeCounter(private val svnClient: SvnClient) {
    private var noOfDiffsLoaded = AtomicInteger(0)
    private var noOfDiffsToLoad = 0

    data class DeferedBranchAndChanges(val branchUrl: String, val author: String, val noOfChanges: Deferred<Int>) {
        suspend fun await(): BranchAndNoOfChanges {
            return BranchAndNoOfChanges(branchUrl, author, noOfChanges.await())
        }
    }
    private fun processDiff(path: String, revisions: Pair<Long, Long>): Deferred<Int> = async {
        val diff = svnClient.loadDiff(path, revisions)
        log.info("Loaded diff ${noOfDiffsLoaded.incrementAndGet()} of $noOfDiffsToLoad")
        DiffInterpreter.retrieveNumLinesChanged(diff)
    }

    private fun countChangesForPath(changedPath: ChangedPath, branches: Collection<RevisionAndBranch>): List<DeferedBranchAndChanges> {
        val branchToRevisions = Multimaps.transformValues(Multimaps.index(branches, { it!!.branch }), { it!!.revision })
        val branchToRevisionRanges = branchToRevisions.asMap().entries.map {
            val revisions = it.value.map { it.number }
            var min = revisions.min()!!
            val max = revisions.max()!!
            if(min == max) {
                min = it.key.trunkRev
            }

            Pair(it.key.baseUrl, Pair(min, max))
        }.toMap()

        return branches.map { Pair(it.branch.baseUrl, it.branch.author) }.toSet().map {
            DeferedBranchAndChanges(it.first, it.second, processDiff(it.first + changedPath, branchToRevisionRanges[it.first]!!))
        }
    }

    fun countChanges(changedPathsAndRevisions: Map<ChangedPath, Collection<RevisionAndBranch>>): Map<ChangedPath, Collection<BranchAndNoOfChanges>> {
        log.info("Found ${changedPathsAndRevisions.size} conflicts. Loading diffs.")
        noOfDiffsToLoad = changedPathsAndRevisions.map { it.value.map { it.branch.baseUrl }.toSet().size }.sum()
        return runBlocking {
             changedPathsAndRevisions.entries.mapIndexed { index, entry ->
                Pair(entry.key, countChangesForPath(entry.key, entry.value))
            }.map {
                Pair(it.first, it.second.map { it.await() })
            }.toMap()
        }
    }
}