#!/usr/bin/env python2.7

from os import environ as env, listdir as ls
import re
import sys

sys.path.append(env["HOME"] + "/research/code")
import soar_exp

class WSD:
	COMMANDS = {"answer":("senseid","source","epmem-recog","smem-recog"), "feedback":("done",)}
	CORPUS_PATH = "{}/Desktop/new-corpus".format(env["HOME"])

	def __init__(self, kernel, agent, params=None):
		self.agent = agent
		self.input_link_wme = agent.GetInputLink()
		self.structure_wme = agent.CreateIdWME(self.input_link_wme, "structure")
		self.request_wme = agent.CreateIdWME(self.input_link_wme, "request")
		self.request_path_wme = agent.CreateIdWME(self.request_wme, "path")
		self.feedback_wme = agent.CreateIdWME(self.input_link_wme, "feedback")
		self.status_wme = None
		if "corpus-path" in params:
			WSD.CORPUS_PATH = params["corpus-path"]
		self.sentence_files = [f for f in ls(WSD.CORPUS_PATH) if re.match("[0-9]*$", f)]
		#self.sentence_files = self.sentence_files + self.sentence_files
		if "max-sentences" in params and params["max-sentences"] > 0:
			self.sentence_files = self.sentence_files[:params["max-sentences"]]
		#self.sentence_files = ["0042", "0047", "0057"]
		self.cur_file_index = 0
		self.cur_tuples = []
		self.cur_word = 0
		self.node_map = {"0":self.structure_wme}
		self.structure_wmes = []
		self.attr_map = {}
		self.request_wmes = []
		self.feedback_wmes = []
		self.answer = None
		self.path = ""
		self.responded = False
		self.last_response = None
		self.last_answer = None
		self.last_source = None
		self.last_epmem_recog = None
		self.last_smem_recog = None
		# register for events
		soar_exp.register_output_callback(kernel, agent, output_callback, self)
		agent.Commit()

	def make_new_request(self):
		self.responded = False
		if self.status_wme:
			self.status_wme.DestroyWME()
			self.destroy_request_structure()
			self.feedback_wmes.reverse()
			for wme in self.feedback_wmes:
				wme.DestroyWME()
			self.feedback_wmes = []
		else:
			self.cur_word = -1
			with open("{}/{}".format(self.CORPUS_PATH, self.sentence_files[self.cur_file_index])) as fd:
				self.cur_tuples = [line.split() for line in fd.read().split("\n") if len(line) > 0]
			self.build_sentence_structure()
		cur_word = self.cur_word
		while self.cur_word == cur_word:
			for index, t in enumerate(self.cur_tuples):
				if index > self.cur_word:
					i, a, v = t
					if re.match("#[^#]*#[^#]*#$", a.strip('"')):
						self.cur_word = index
						self.build_request_structure()
						self.status_wme = self.agent.CreateStringWME(self.input_link_wme, "status", "request")
						break
			else:
				self.destroy_sentence_structure()
				self.cur_file_index = self.cur_file_index + 1
				if self.cur_file_index < len(self.sentence_files):
					self.cur_word = -1
					cur_word = -1
					with open("{}/{}".format(self.CORPUS_PATH, self.sentence_files[self.cur_file_index])) as fd:
						self.cur_tuples = [line.split() for line in fd.read().split("\n") if len(line) > 0]
					self.build_sentence_structure()
				else:
					self.domain_complete()
					self.cur_word = -2

	def domain_complete(self):
		self.status_wme = self.agent.CreateStringWME(self.input_link_wme, "status", "done")

	def destroy_sentence_structure(self):
		self.structure_wmes.reverse()
		for wme in self.structure_wmes:
			wme.DestroyWME()
		self.structure_wmes = []
		for num, wme in self.node_map.items():
			if num != "0":
				del self.node_map[num]

	def destroy_request_structure(self):
		self.request_wmes.reverse()
		for wme in self.request_wmes:
			wme.DestroyWME()
		self.request_wmes = []

	def build_sentence_structure(self):
		self.structure_wmes.append(self.agent.CreateIntWME(self.input_link_wme, "sentence-num", self.cur_file_index))
		for t in self.cur_tuples:
			i, a, v = t
			identifier = self.node_map[i]
			a = re.sub("#[^#]*#([^#]*)#", r"\1", a).strip('"')
			a = re.sub("#[^#]*#([^#]*)$", r"\1", a)
			a = re.sub("([^#]*)#([^#]*)$", r"\1\2", a)
			a = re.sub("^[^a-zA-Z0-9]*", "", a)
			a = re.sub("[^a-zA-Z0-9]*$", "", a)
			child = self.agent.CreateIdWME(identifier, a)
			self.node_map[v] = child
			self.structure_wmes.append(child)
			self.attr_map[v] = a

	def build_request_structure(self):
		self.answer = re.sub("#([^#]*)#[^#]*#", r"\1", self.cur_tuples[self.cur_word][1])
		self.path = []
		parent, attribute, value = self.cur_tuples[self.cur_word]
		path = [self.attr_map[value],]
		while parent != "0":
			for t in self.cur_tuples:
				if t[2] == parent:
					parent = t[0]
					path.append(t[1])
					break
		path.reverse()
		parent = self.request_path_wme
		for attribute in path:
			wme = self.agent.CreateIdWME(parent, attribute)
			self.request_wmes.append(wme)
			parent = wme
			self.path.append(attribute)
		self.request_wmes.append(self.agent.CreateStringWME(self.request_wme, "word", path[-1]))
		# this links to past "string" and before the word
		self.request_wmes.append(self.agent.CreateSharedIdWME(self.request_wme, "path-direct", self.request_wmes[-3]))

	def give_feedback(self, commands):
		senseid = commands["answer"]["senseid"]
		self.status_wme.DestroyWME()
		self.status_wme = self.agent.CreateStringWME(self.input_link_wme, "status", "feedback")
		self.feedback_wmes.append(self.agent.CreateStringWME(self.feedback_wme, "correct", "yes" if senseid == self.answer else "no"))
		self.feedback_wmes.append(self.agent.CreateStringWME(self.feedback_wme, "senseid", self.answer))
		self.last_response = senseid
		self.last_answer = self.answer
		self.last_source = commands["answer"]["source"]
		self.last_epmem_recog = "no" if "epmem-recog" not in commands["answer"] else "yes"
		self.last_smem_recog = "no" if "smem-recog" not in commands["answer"] else "yes"
		self.responded = True

	def process_actions(self, commands, mapping):
		if len(commands) > 1:
			for cmd in mapping.values():
				cmd.AddStatusError()
		else:
			if "feedback" in commands:
				self.make_new_request()
				mapping["feedback"].AddStatusComplete()
			elif "answer" in commands:
				self.give_feedback(commands)
				mapping["answer"].AddStatusComplete()
			else:
				for cmd in mapping.values():
					cmd.AddStatusError()
		self.agent.Commit()
		self.agent.ClearOutputLinkChanges()

def output_callback(mid, user_data, agent, message):
	user_data.process_actions(*soar_exp.parse_output_commands(agent, user_data.COMMANDS))
