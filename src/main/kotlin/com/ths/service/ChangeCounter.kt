package com.ths.service

import com.google.common.collect.Multimaps
import com.ths.model.RevisionAndBranch
import com.ths.svn.SvnClient
import org.apache.log4j.Logger

private val log = Logger.getLogger(BranchAndNoOfChanges::class.java)

data class BranchAndNoOfChanges(val branchUrl: String, val author: String, val noOfChanges: Int)
class ChangeCounter(private val svnClient: SvnClient) {
    private fun processDiff(path: String, revisions: Pair<Long, Long>): Int {
        val diff = svnClient.loadDiff(path, revisions)
        return DiffInterpreter.retrieveNumLinesChanged(diff)
    }

    private fun countChangesForPath(changedPath: ChangedPath, branches: Collection<RevisionAndBranch>): List<BranchAndNoOfChanges> {
        log.debug("Processing log for $changedPath")
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
            BranchAndNoOfChanges(it.first, it.second, processDiff(it.first + changedPath, branchToRevisionRanges[it.first]!!))
        }
    }

    fun countChanges(changedPathsAndRevisions: Map<ChangedPath, Collection<RevisionAndBranch>>): Map<ChangedPath, Collection<BranchAndNoOfChanges>> {
        val noOfChangedPaths = changedPathsAndRevisions.size
        return changedPathsAndRevisions.entries.mapIndexed { index, entry ->
            log.info("Processing conflicting path ${index + 1} of $noOfChangedPaths")
            Pair(entry.key, countChangesForPath(entry.key, entry.value))
        }.toMap()
    }
}