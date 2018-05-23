package com.ths.svn

import com.ths.model.Branch
import org.apache.log4j.Logger
import org.tmatesoft.svn.core.SVNDepth
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.wc.SVNClientManager
import org.tmatesoft.svn.core.wc.SVNRevision
import org.tmatesoft.svn.core.wc.SVNRevisionRange
import org.tmatesoft.svn.core.wc.SVNWCUtil
import java.io.ByteArrayOutputStream

private val log: Logger = Logger.getLogger(SvnClient::class.java)

class SvnClient(userName: String, password: String, private val repositoryUrl: String, private val branchesUrl: String, trunkPath: String, startRevision: Long, endRevision: Long) {
    private val clientManager = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), userName, password)
    private val logClient = clientManager.logClient
    private val diffClient = clientManager.diffClient
    private val uri = SVNURL.parseURIEncoded(repositoryUrl)
    private val revisionRange = SVNRevisionRange(SVNRevision.create(startRevision), SVNRevision.create(endRevision))
    private val paths = Array(1, {"."})
    private val branchBuilder = SvnHandler(trunkPath)

    fun loadLogs(): List<Branch> {
        log.info("Starting SVN log download for revisions ${revisionRange.startRevision.number} - ${revisionRange.endRevision.number}")
        logClient.doLog(
                uri,
                paths,
                null,
                listOf(revisionRange),
                false,
                true,
                false,
                0,
                null,
                branchBuilder
        )
        return branchBuilder.assembleBranches()
    }

    fun loadDiff(pathUrl: String, revisionRange: Pair<Long, Long>): String {
        log.debug("Downloading diff for $pathUrl")
        val branchUri = SVNURL.parseURIEncoded(repositoryUrl + pathUrl)
        ByteArrayOutputStream().use {
            diffClient.doDiff(
                    branchUri,
                    SVNRevision.create(revisionRange.first),
                    branchUri,
                    SVNRevision.create(revisionRange.second),
                    SVNDepth.IMMEDIATES,
                    false,
                    it
            )
            return it.toString("UTF-8")
        }
    }
}