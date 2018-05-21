package com.ths.service

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.ths.model.Branch
import com.ths.model.Revision
import com.ths.model.RevisionAndBranch
import java.util.*

typealias ChangedPath = String
class ConflictDetector(_branches: Collection<Branch>) {
    private val branches = Collections.unmodifiableCollection(_branches)

    /**
     * @return Map with paths that have at least 2 modifications
     */
    fun findConflicts(): Map<ChangedPath, Collection<RevisionAndBranch>> {
        val pathToRevisions = ArrayListMultimap.create<String, RevisionAndBranch>()
        branches.forEach { pathToRevisions.putChangesFromBranch(it) }
        return pathToRevisions.asMap().entries.filter { it.value.map { it.branch.baseUrl }.toSet().size > 1 }.map { Pair(it.key, it.value) }.toMap()
    }

    private fun Multimap<String, RevisionAndBranch>.putChangesFromRevision(rev: RevisionAndBranch) =
        rev.revision.changes.forEach { put(it.path, rev) }

    private fun Multimap<String, RevisionAndBranch>.putChangesFromBranch(branch:  Branch)  =
        branch.revisions.forEach {  putChangesFromRevision(RevisionAndBranch(it, branch))  }
}