package com.ths.svn

import com.ths.model.Branch
import com.ths.model.Change
import com.ths.model.ChangeType
import com.ths.model.Revision
import org.tmatesoft.svn.core.ISVNLogEntryHandler
import org.tmatesoft.svn.core.SVNLogEntry
import java.util.*

class SvnHandler(private val trunkPath: String): ISVNLogEntryHandler {
    private val result = ArrayList<Revision>()
    val revisions: List<Revision> get() = Collections.unmodifiableList(result)

    override fun handleLogEntry(logEntry: SVNLogEntry) {
        result.add(Revision(logEntry.revision, logEntry.author, logEntry.date, logEntry.changedPaths.map {
            val changeEntry = it.value
     Change(
                changeEntry.path,
                if(changeEntry.copyPath == trunkPath) ChangeType.BRANCH else ChangeType.findByChar(changeEntry.type),
                changeEntry.copyPath,
                changeEntry.copyRevision
            )
        }))
    }

    fun assembleBranches(): List<Branch> {
        return revisions
                .filter { it.isTrunkBranch() }
                .map { Branch(it.branchUrl!!, it.trunkRevision!!, it.author, findRevisionsForBranch(it.branchUrl!!)) }
    }
    private fun findRevisionsForBranch(branchUrl: String): List<Revision> = revisions.filter { !it.isTrunkBranch() && it.isForUrl(branchUrl)}
}