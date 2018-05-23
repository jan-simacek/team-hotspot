package com.ths.cli

import org.kohsuke.args4j.Option

class CmdlineArgs {
    @Option(name = "--user-name", aliases = ["-u"], usage = "SVN Login username", required = true)
    var userName: String = ""
    @Option(name = "--password", aliases = ["-p"], usage = "SVN Password", required = true)
    var password: String = ""
    @Option(name = "--repository-url", aliases = ["-r"],  usage = "Repository root, e.g.: https://wfmsvn.infor.com/svn/wfm", required = true)
    var repositoryUrl: String = ""
    @Option(name = "--trunk-path", aliases = ["-t"], usage = "Relative path to trunk, e.g.: /WORKBRAIN/Source/trunk", required = true)
    var trunkPath: String = ""
    @Option(name = "--branches-path", aliases = ["-b"], usage = "Relative path to branches root, e.g.: /WORKBRAIN/Source/projects/Feature_branches", required = true)
    var branchesPath: String = ""
    @Option(name = "--start-revision", aliases = ["-s"], usage = "First revision to be examined", required = true)
    var startRevision: Long = 1
    @Option(name = "--end-revision", aliases = ["-e"], usage = "Last revision to be examined", required = true)
    var endRevision: Long = Long.MAX_VALUE
    @Option(name = "--output-file", aliases = ["-o"], usage = "File where to write the results in JSON e.g.: output.json or c:/temp/output.json", required = true)
    var outputFile: String = "conflicts.json"
}