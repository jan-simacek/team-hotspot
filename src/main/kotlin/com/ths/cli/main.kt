package com.ths.cli

import com.ths.output.OutputSerializer
import com.ths.output.writeToFile
import com.ths.service.ChangeCounter
import com.ths.service.ConflictDetector
import com.ths.svn.SvnClient
import java.io.File

const val ARG_COUNT = 8

fun main(args: Array<String>) {

    val cmdlineArgs = parseArgs(args) ?: return

    val svnClient = SvnClient(
            userName = cmdlineArgs.userName,
            password = cmdlineArgs.password,
            repositoryUrl = cmdlineArgs.repositoryUrl,
            branchesUrl = cmdlineArgs.branchesPath,
            trunkPath = cmdlineArgs.trunkPath,
            startRevision = cmdlineArgs.startRevision,
            endRevision = cmdlineArgs.endRevision
    )
    val conflicts = ConflictDetector(svnClient.loadLogs()).findConflicts()
    OutputSerializer
            .convertToOutputDtos(ChangeCounter(svnClient).countChanges(conflicts))
            .writeToFile(File(cmdlineArgs.outputFile))
}

fun parseArgs(args: Array<String>): CmdlineArgs? {
    if(args.size != ARG_COUNT) {
        printUsage()
        return null
    }
    return try {
        CmdlineArgs(
                userName = args[0],
                password = args[1],
                repositoryUrl = args[2],
                trunkPath = args[3],
                branchesPath = args[4],
                startRevision = args[5].toLong(),
                endRevision = args[6].toLong(),
                outputFile = args[7]
        )
    } catch(e: Exception) {
        printUsage();
        null;
    }
}

private fun printUsage() {
    println("Usage:")
    println("hs-server <user_name> <password> <repository_root_url> <trunk_path> <branches_path> <start_revision> <end_revision> <output_file>")
    println("Example: hs-server testuser mypass https://infor.com/nxs/svn/pokus /trunk /branches 123 124 changes.json")
}

data class CmdlineArgs(
    val userName: String,
    val password: String,
    val repositoryUrl: String,
    val trunkPath: String,
    val branchesPath: String,
    val startRevision: Long,
    val endRevision: Long,
    val outputFile: String
)