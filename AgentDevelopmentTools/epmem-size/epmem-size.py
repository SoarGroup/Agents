#!/usr/bin/env python2

from os import environ as env
from os.path import expanduser, realpath
import re
import sys

if "DYLD_LIBRARY_PATH" in env:
	LIB_PATH = env["DYLD_LIBRARY_PATH"]
elif "LD_LIBRARY_PATH" in env:
	LIB_PATH = env["LD_LIBRARY_PATH"]
else:
	print("Soar LIBRARY_PATH environment variable not set; quitting")
	exit(1)
sys.path.append(LIB_PATH)
import Python_sml_ClientInterface as sml

def print_callback(mid, data, agent, message):
	print(message)

def main():
	if len(sys.argv) != 3:
		print("usage: epmem_size EPMEM_DB SAMPLE_INTERVAL")
		exit(1)
	kernel = sml.Kernel.CreateKernelInCurrentThread()
	if kernel.HadError():
		print("Error creating kernel: " + kernel.GetLastErrorDescription())
		exit(1)
	agent = kernel.CreateAgent("epmem-size-agent")
	if not agent:
		print("Error creating agent: " + kernel.GetLastErrorDescription())
		exit(1)
	agent.RegisterForPrintEvent(sml.smlEVENT_PRINT, print_callback, None)
	commands = [
			"epmem --set path {}".format(realpath(expanduser(sys.argv[1]))),
			"epmem -p 1",
			]
	for command in commands:
		result = agent.ExecuteCommandLine(command)
		if not agent.GetLastCommandLineResult():
			print(result)
			exit(1)
	num_episodes = int(agent.ExecuteCommandLine("epmem --stats time"))
	prev_size = 0
	for episode in range(1, num_episodes, sys.argv[2]):
		epviz = agent.ExecuteCommandLine("epmem --viz {}".format(episode)).split("\n")
		size = len([edge for edge in epviz if re.match("ID_[0-9]+ -> ", edge)])
		print("{} {} {}".format(episode, size, size - prev_size))
		prev_size = size
	kernel.Shutdown()
	del kernel

if __name__ == "__main__":
	main()
