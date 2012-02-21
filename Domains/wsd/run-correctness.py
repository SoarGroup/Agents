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
	params["agent"] = ("sfef", "sfer", "sfed", "sref", "srer", "sred", "sdef", "sder", "sded")
	params["recognition"] = ("on",)
	params["trial"] = list(range(0, 1))
	params["max-sentences"] = (5000,)

	params["agent"] = ("sfer", "sfed")
	params["max-sentences"] = (5000,)

	commands = (
			"source {home}/research/code/wsd/agent/{agent}.soar",
			"smem --set recognition {recognition}",
			"epmem --set recognition {recognition}",
			"w 0",
			)
	reporters = (
			soar_exp.branch_name,
			(lambda param_map, domain, agent: ("sentence-num", domain.cur_file_index)),
			(lambda param_map, domain, agent: ("path", "/".join(domain.path))),
			(lambda param_map, domain, agent: ("word", domain.path[-1])),
			(lambda param_map, domain, agent: ("response", domain.last_response)),
			(lambda param_map, domain, agent: ("answer", domain.last_answer)),
			(lambda param_map, domain, agent: ("source", domain.last_source)),
			(lambda param_map, domain, agent: ("epmem_recog", domain.last_epmem_recog)),
			(lambda param_map, domain, agent: ("smem_recog", domain.last_smem_recog)),
			)
	# run Soar
	kernel = soar_exp.create_kernel()
	for param_map in soar_exp.param_permutations(params):
		agent = soar_exp.create_agent(kernel, "agent-" + "-".join(str(val) for val in param_map.values()))
		wsd = WSD(kernel, agent, param_map)
		soar_exp.register_output_change_callback(kernel, agent, soar_exp.print_report_row, soar_exp.report_data_wrapper(param_map, wsd, reporters, (lambda param_map, domain, agent: domain.responded)))
		soar_exp.run_parameterized_commands(agent, param_map, commands)
		agent.ExecuteCommandLine("run")
		#soar_exp.cli(agent)
		kernel.DestroyAgent(agent)
	kernel.Shutdown()
	del kernel

if __name__ == "__main__":
	if len(sys.argv) > 1:
		from time import sleep
		sleep(int(sys.argv[1]))
	main()
