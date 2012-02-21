#!/usr/bin/env python

import re
import sys

def state2dot(state):
	state = "\n".join(line.strip() for line in state.split("\n"))
	state = re.sub(r'\n\^', " ^", state)
	#state = re.sub(" \+", "", state)
	while re.search("\([^ ]+ \^[^ ]+ (\|[^|]*\||[^| )]+)( \+)? \^", state):
		state = re.sub("\(([^ ]+) (\^[^ ]+ (\|[^|]*\||[^| )]+)( \+)?) \^", r"(\1 \2)\n(\1 ^", state)
	lines = set()
	count = 0
	for line in state.split("\n"):
		line = line.strip()
		if not line:
			continue
		value = re.sub("^\([^ ]+ \^[^ ]+ (.*?)( \+)?\)$", r'\1', line)
		if re.match("^[A-Z][0-9]+$", value):
			lines.add(re.sub("^\(([^ ]+) \^([^ ]+) ([^ ]+?)(( \+)?)\)$", r'"\1" -> "\3" [label="\2\4"]', line))
		elif value.startswith("@"):
			lines.add(re.sub("^\(([^ ]+) \^([^ ]+) ([^ ]+?)(( \+)?)\)$", r'"\1" -> "\3" [label="\2\4"]', line))
			lines.add('"' + value + '" [shape="doublecircle"]')
			count += 1
		else:
			lines.add(re.sub("^\(([^ ]+) \^([^ ]+) (.*?)(( \+)?)\)$", r'"\1" -> "temp' + str(count) + r'" [label="\2\4"]', line))
			lines.add('"temp' + str(count) + '" [label="' + value + '", shape="box"]')
			count += 1
	result = ["digraph {"]
	result.append('	node [shape="circle"];')
	result.append("	" + ";\n	".join(lines) + ";")
	result.append("}")
	result = "\n".join(result)
	return result

if __name__ == "__main__":
	text = ""
	if len(sys.argv) > 1:
		result = []
		for file in sys.argv[1:]:
			with open(file, "r") as fd:
				result.append(fd.read())
		text = "".join(result)
	elif not stdin.isatty():
		text = stdin.read()
	print(state2dot(text))
