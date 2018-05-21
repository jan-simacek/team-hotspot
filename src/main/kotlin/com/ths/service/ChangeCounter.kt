package com.ths.service

import com.google.common.collect.Multimaps
import com.ths.model.RevisionAndBranch
import com.ths.svn.SvnClient

data class BranchAndNoOfChanges(val branchUrl: String, val noOfChanges: Int)
class ChangeCounter(private val svnClient: SvnClient) {
    private fun processDiff(path: String, revisions: Pair<Long, Long>): Int {
        val diff = svnClient.loadDiff(path, revisions)
        return DiffInterpreter.retrieveNumLinesChanged(diff)
    }

    private fun countChangesForPath(changedPath: ChangedPath, branches: Collection<RevisionAndBranch>): List<BranchAndNoOfChanges> {
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

        return branches.map { it.branch.baseUrl }.toSet().map {
            BranchAndNoOfChanges(it, processDiff(it + changedPath, branchToRevisionRanges[it]!!))
        }
    }

    fun countChanges(changedPathsAndRevisions: Map<ChangedPath, Collection<RevisionAndBranch>>): Map<ChangedPath, Collection<BranchAndNoOfChanges>> {
        return changedPathsAndRevisions.entries.map {
            Pair(it.key, countChangesForPath(it.key, it.value))
        }.toMap()
    }
}