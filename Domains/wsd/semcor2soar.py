#!/usr/bin/env python3

# this script requires the RASP parser (http://ilexir.co.uk/applications/rasp/)

from os import environ as env
from subprocess import Popen, PIPE, check_output
from math import ceil, log10
import re
import sys

sys.path.append(env["HOME"] + "/projects/pegparse")
from pegparse import create_parser

RASP = "{}/rasp3os/scripts/rasp.sh".format(env["HOME"])

def is_word(word):
	return re.search("[0-9A-Za-z]", word)

def ast2sentence(ast):
	cache = []
	if ast.term == "Branch":
		for child in ast.descendants("Exp/*"):
			cache.extend(ast2sentence(child))
	elif ast.term == "Leaf":
		cache.append(ast.match.strip('"'))
	while "'s+" in cache:
		cache.remove("'s+")
	return cache

def ast2tuple(ast, parent=0, uid=None):
	if uid == None:
		uid = parent + 1
	cache = []
	myid = uid
	uid += 1
	pos = ast.first_descendant("PartOfSpeech").match.strip('"')
	cache.append((parent, pos, myid))
	string = " ".join(child.match.strip('"') for child in ast.descendants("Exp/Leaf"))
	if is_word(string):
		cache.append((myid, "string", uid))
		cache.append((uid, re.sub(" ", "_", string), uid+1))
		uid += 2
	for child in ast.descendants("Exp/Branch"):
		tuples, uid = ast2tuple(child, myid, uid)
		cache.extend(tuples)
	return cache, uid

def replaceLeaves(ast, tagged):
	if ast.term == "Branch":
		for child in ast.descendants("Exp/*"):
			nast, tagged = replaceLeaves(child, tagged)
	elif ast.term == "Leaf" and is_word(ast.match):
		if ast.match.strip('"') != "'s+":
			ast.match = re.sub("^[^#0-9A-Za-z]*(.*[#0-9A-Za-z])[^#0-9A-Za-z]*$", r"\1", tagged[0])
			tagged = tagged[1:]
	return ast, tagged

def tagfile2data(sentences):
	# convert tagfile to sentences
	sentences = [re.sub("cmd=done.*lexsn=([^ ]*)>([^<]*)<[^>]*>$", r">#\1#\2#", line) for line in sentences]
	sentences = [re.sub("<[^>]*>", "", line) for line in sentences]
	sentences = [sentence.strip() for sentence in " ".join(sentences).split("  ") if is_word(sentence)]
	sentences = [re.sub("^' ", "'", sentence) for sentence in sentences]
	sentences = [re.sub("_", " ", sentence) for sentence in sentences]
	sentences = [re.sub(" ([^[(`0-9A-Za-z#$-])", r"\1", sentence) for sentence in sentences]
	sentences = [re.sub(r"(`|``|\(|\[) ", r"\1", sentence) for sentence in sentences]
	sentences = [re.sub(r" n't\b", "n't", sentence) for sentence in sentences]
	sentences = [re.sub("\. ", " ", sentence) for sentence in sentences]
	# create parse tree
	sentence_set = set()
	digits = int(ceil(log10(len(sentences))))
	parser = create_parser("tree-grammar")
	for index, sentence in enumerate(sentences):
		if sentence in sentence_set:
			continue
		sentence_set.add(sentence)
		filenum = ("{:0" + str(digits) + "d}").format(index + 1)
		cleaned = re.sub("#[^#]*#([^#]*)#", r"\1", sentence)
		rasp = Popen((RASP, "-p" , "-os -u"), stdin=PIPE, stdout=PIPE)
		parse_tree = "\n".join(rasp.communicate(cleaned.encode())[0].decode().split("\n")[2:]).strip()
		ast, parsed = parser.parse(parse_tree, "Tree")
		if ast and parsed == len(parse_tree):
			ast = ast.first_descendant("Branch")
			tree_length = len([word for word in ast2sentence(ast) if is_word(word)])
			sentence_length = len([word for word in sentence.split() if is_word(word)])
			if tree_length == sentence_length:
				tagged = [word for word in sentence.split() if is_word(word)]
				rast, depleted = replaceLeaves(ast, tagged)
				if any(re.match("#[^#]*#[^#]*#$", word) for word in ast2sentence(rast)):
					with open("/Users/justinnhli/Desktop/new-corpus/"+filenum, "w") as fd:
						fd.writelines("{} {} {}\n".format(i, a, re.sub(" ", "_", str(v))) for i, a, v in ast2tuple(rast)[0])
					print("{}: success".format(filenum))
				else:
					print("{}: no tagged words".format(filenum))
			else:
				print("{}: cannot match parse tree with sentence".format(filenum))
		else:
			print("{}: parse unsuccesful".format(filenum))
			print(sentence)
			print(parse_tree)

if __name__ == "__main__":
	if len(sys.argv) < 2:
		print("usage: {} TAGFILE ...".format(sys.argv[0]))
		exit(1)
	text = ""
	for f in sys.argv[1:]:
		with open(f) as fd:
			text += fd.read()
	if text:
		tagfile2data(text.split("\n"))
