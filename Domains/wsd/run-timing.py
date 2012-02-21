#!/usr/bin/env python2.7

from os import environ as env
import re
import sys

sys.path.append(env["HOME"] + "/research/code")
import soar_exp

from wsd import WSD

def main():
	params = {}
	params["home"] = (env["HOME"],)
	params["corpus-path"] = ("{}/Desktop/corpus".format(env["HOME"]),)
	params["agent"] = ("sded",)
	params["recognition"] = ("on", "off")
	params["recognition"] = ("on",)
	params["trial"] = list(range(0, 10))
	params["max-sentences"] = (100,)
	commands = (
			"source {home}/research/code/wsd/agent/{agent}.soar",
			"smem --set recognition {recognition}",
			"epmem --set recognition {recognition}",
			"w 0",
			)
	reporters = (
			soar_exp.branch_name,
			soar_exp.avg_decision_time,
			soar_exp.max_decision_time,
			)
	# run Soar
	kernel = soar_exp.create_kernel()
	for param_map in soar_exp.param_permutations(params):
		agent = soar_exp.create_agent(kernel, "agent-" + "-".join(str(val) for val in param_map.values()))
		wsd = WSD(kernel, agent, param_map)
		soar_exp.register_destruction_callback(kernel, agent, soar_exp.print_report_row, soar_exp.report_data_wrapper(param_map, wsd, reporters))
		soar_exp.run_parameterized_commands(agent, param_map, commands)
		agent.ExecuteCommandLine("run")
		#soar_exp.cli(agent)
		kernel.DestroyAgent(agent)
		del agent
	kernel.Shutdown()
	del kernel

if __name__ == "__main__":
	if len(sys.argv) > 1:
		from time import sleep
		sleep(int(sys.argv[1]))
	main()
