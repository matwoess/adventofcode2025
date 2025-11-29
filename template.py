from pathlib import Path
import numpy as np


def part1(data: list[int]) -> int:
    return 0


def part2(data: list[int]) -> int:
    return 0


if __name__ == '__main__':
    raw_input = Path('examples/dayXX.txt').read_text(encoding='utf-8')
    input_data = [int(line) for line in raw_input.splitlines()]
    answer1 = part1(input_data)
    print('Answer1:', answer1)  # ANSWER
    answer2 = part2(input_data)
    print('Answer2:', answer2)  # ANSWER