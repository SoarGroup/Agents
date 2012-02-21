#!/usr/bin/env python2.7

from itertools import product
from os import environ as env, fsync
from subprocess import call, check_output, CalledProcessError, STDOUT
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

# low level Soar functions

def create_kernel():
	kernel = sml.Kernel.CreateKernelInCurrentThread()
	if not kernel or kernel.HadError():
		print("Error creating kernel: " + kernel.GetLastErrorDescription())
		exit(1)
	return kernel

def create_agent(kernel, name):
	agent = kernel.CreateAgent("agent")
	if not agent:
		print("Error creating agent: " + kernel.GetLastErrorDescription())
		exit(1)
	return agent

# mid-level framework

def cli(agent):
	cmd = raw_input("soar> ")
	while cmd not in ("exit", "quit"):
		if cmd:
			print(agent.ExecuteCommandLine(cmd).strip())
		cmd = raw_input("soar> ")

def parameterize_commands(param_map, commands):
	return [cmd.format(**param_map) for cmd in commands]

def run_parameterized_commands(agent, param_map, commands):
    for cmd in parameterize_commands(param_map, commands):
		result = agent.ExecuteCommandLine(cmd)

def param_permutations(params):
	keys = sorted(params.keys())
	for values in product(*(params[key] for key in keys)):
		yield dict(zip(keys, values))

# IO

def parse_output_commands(agent, structure):
	commands = {}
	mapping = {}
	for cmd in range(0, agent.GetNumberCommands()):
		error = False
		command = agent.GetCommand(cmd)
		cmd_name = command.GetCommandName()
		if cmd_name in structure:
			parameters = {}
			for param_name in structure[cmd_name]:
				param_value = command.GetParameterValue(param_name)
				if param_value:
					parameters[param_name] = param_value
			if not error:
				commands[cmd_name] = parameters
				mapping[cmd_name] = command
		else:
			error = True
		if error:
			command.AddStatusError()
	return commands, mapping

def dot_to_input(edges):
	pass

# callback registry

def register_print_callback(kernel, agent, function, user_data=None):
	agent.RegisterForPrintEvent(sml.smlEVENT_PRINT, function, user_data)

def register_output_callback(kernel, agent, function, user_data=None):
	agent.RegisterForRunEvent(sml.smlEVENT_AFTER_OUTPUT_PHASE, function, user_data)

def register_output_change_callback(kernel, agent, function, user_data=None):
	kernel.RegisterForUpdateEvent(sml.smlEVENT_AFTER_ALL_GENERATED_OUTPUT, function, user_data)

def register_destruction_callback(kernel, agent, function, user_data=None):
	agent.RegisterForRunEvent(sml.smlEVENT_AFTER_HALTED, function, user_data)

# callback functions

def callback_print_message(mid, user_data, agent, message):
	print(message.strip())

def print_report_row(mid, user_data, agent, *args):
	condition = user_data["condition"]
	param_map = user_data["param_map"]
	domain = user_data["domain"]
	reporters = user_data["reporters"]
	if condition(param_map, domain, agent):
		pairs = []
		pairs.extend("=".join([k, str(v)]) for k, v in param_map.items())
		pairs.extend("{}={}".format(*reporter(param_map, domain, agent)) for reporter in reporters)
		print(" ".join(pairs))

def report_data_wrapper(param_map, domain, reporters, condition=None):
	if condition is None:
		condition = (lambda param_map, domain, agent: True)
	return {
			"condition": condition,
			"param_map": param_map,
			"domain": domain,
			"reporters": reporters,
			}

# common reporters

def branch_name(param_map, domain, agent):
	result = re.sub(".* ", "", check_output(("ls", "-l", "{}/SoarSuite/Core".format(env["HOME"])))).strip()
	return ("branch", result)

def avg_decision_time(param_map, domain, agent):
	result = re.sub(".*\((.*) msec/decision.*", r"\1", agent.ExecuteCommandLine("stats"), flags=re.DOTALL)
	return ("avg_dc_msec", result)

def max_decision_time(param_map, domain, agent):
	result = re.sub(".*  Time \(sec\) *([0-9.]*).*", r"\1", agent.ExecuteCommandLine("stats -M"), flags=re.DOTALL)
	return ("max_dc_msec", float(result) * 1000)

"""
stats
39952 decisions (1.172 msec/decision)
309943 elaboration cycles (7.758 ec's per dc, 0.151 msec/ec)
309943 inner elaboration cycles
136794 p-elaboration cycles (3.424 pe's per dc, 0.342 msec/pe)
391783 production firings (1.264 pf's per ec, 0.120 msec/pf)
3174729 wme changes (1587424 additions, 1587305 removals)
WM size: 119 current, 1497.991 mean, 18978 maximum
"""

"""
stats -M
Single decision cycle maximums:
Stat             Value       Cycle
---------------- ----------- -----------
      Time (sec)   0.050799       11634
EpMem Time (sec)   0.000000           0
 SMem Time (sec)   0.000000           0
      WM changes      19096       11634
    Firing count        792       11634
"""

# soar code management

def make_branch(branch):
	try:
		stdout = check_output(("make-branch", branch), stderr=STDOUT)
		return True
	except CalledProcessError as cpe:
		return False

if __name__ == "__main__":
	kernel = create_kernel()
	agent = create_agent(kernel, "agent")
	register_print_callback(kernel, agent, callback_print_message, None)
	for source in sys.argv[1:]:
		print(agent.ExecuteCommandLine("source " + source))
	cli(agent)
	kernel.DestroyAgent(agent)
	kernel.Shutdown()
	del kernel
