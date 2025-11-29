package day01

import java.io.File



fun preProcessData(lines: List<String>): List<String> {
    return lines
}

fun part1(data: List<String>): Int {
    return 0
}

fun part2(data: List<String>): Int {
    return 0
}


fun main() {
    val input = File("examples/day01.txt").readLines()
    val data = preProcessData(input)
    val answer1 = part1(data)
    println("Answer 1: $answer1") // ANSWER
    val answer2 = part2(data)
    println("Answer 2: $answer2") // ANSWER
}