package com.ths.cli

import com.ths.output.OutputSerializer
import com.ths.output.writeToFile
import com.ths.service.ChangeCounter
import com.ths.service.ConflictDetector
import com.ths.svn.SvnClient
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import java.io.File

fun main(args: Array<String>) {

    val cmdlineArgs = parseArgs(args)

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

fun parseArgs(args: Array<String>): CmdlineArgs {
    val cmdlineArgs = CmdlineArgs()
    val cmdLineParser = CmdLineParser(cmdlineArgs)
    try {
        cmdLineParser.parseArgument(args.toList())
    } catch (e: CmdLineException) {
        System.err.println("Invalid arugments: ${e.message}")
        System.err.println("Usage:")
        cmdLineParser.printUsage(System.err)
        System.exit(1)
    }
    return cmdlineArgs
}

private fun printUsage() {
    println("Usage:")
    println("hs-server <user_name> <password> <repository_root_url> <trunk_path> <branches_path> <start_revision> <end_revision> <output_file>")
    println("Example: hs-server testuser mypass https://infor.com/nxs/svn/pokus /trunk /branches 123 124 changes.json")
}

