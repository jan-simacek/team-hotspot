package com.ths.output

data class OutputDto(val paths: List<PathOutputDto>)

data class PathOutputDto(val path: String, val branches: List<BranchOutputDto>)

data class BranchOutputDto(val branch: String, val noOfChanges: Int)