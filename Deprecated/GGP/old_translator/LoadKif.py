#!/usr/bin/python

import os, sys
import RuleParser
from ElementGGP import ElementGGP

def help():
	print 'usage: %s [kif file]' % sys.argv[0]
	print '''
Translates a kif domain description read from standard input or specified
file into soar productions that simulate the domain, printed to standard
out.'''
	sys.exit(0)
	
infile = sys.stdin
for a in sys.argv[1:]:
	if a == '-h':
		help()
	else:
		infile = open(a, 'r')

description = " ".join(line.partition('#')[0].strip() for line in infile)
RuleParser.TranslateDescription("game", ElementGGP("(%s)" % description), sys.stdout)
