import random
import pdb
from rule import Rule

class State:
	def __init__(self, preds, children):
		self.preds = frozenset(preds)
		self.__children = children
	
	def get_child(self, action):
		return self.__children[action]

	def make_max_rules(self, rules, all_preds):
		ncs = all_preds - self.preds
		for a, c in self.__children.items():
			rules.append(Rule(self.preds, ncs, a, c.preds))
			c.make_max_rules(rules, all_preds)

	def next_is_goal(self, action):
		return len(self.__children[action].preds) == 0

	def consistent_with(self, r):
		"""Returns True if a rule either doesn't fire in the state or fires
		consistently with the children of r"""
		return (r.doesnt_fire_in(self)) or (r.get_rhs() <= self.__children[r.get_action()].preds)
	
	def implemented_by(self, rules):
		"""Returns True if a set of rules exactly generates this state's children"""

		gen_sets = {}
		rules_fired = {}
		for r in rules:
			if r.fires_in(self):
				if not self.next_is_goal(r.get_action()) and r.is_goal_rule():
					return False
				gen_sets.setdefault(r.get_action(), set()).update(r.get_rhs())
				rules_fired.setdefault(r.get_action(), []).append(r)

		for a in self.__children:
			if a not in gen_sets: 
				return False
			if gen_sets[a] != self.__children[a].preds: 
				return False

		return True

	def get_graphviz(self):
		s = ''
		my_label = str(self)
		for a, c in self.__children.items():
			s += '%s -> %s [label="%s"];\n' % (my_label, str(c), a)
			if len(c.preds) > 0:
				s += c.get_graphviz()
		return s

	def get_all_states(self, states):
		states.append(self)
		for a, c in self.__children.items():
			c.get_all_states(states)
	
	def map(self, func):
		"""Like the Lisp map function"""

		func(self)
		for a in self.__children:
			map(self.__children[a], func)
	
	def __str__(self):
		if len(self.preds) == 0:
			return 'goal'
		l = list(self.preds)
		l.sort()
		return ''.join(l)

class Rules2FS:
	""" maintains a cache of which states rules fire in """
	def __init__(self, states):
		self.__states = states
		self.__r2fs = {}
	
	def __getitem__(self, rule):
		if rule in self.__r2fs:
			return self.__r2fs[rule]
		else:
			fs = []
			for s in self.__states:
				if rule.fires_in(s):
					fs.append(s)
			self.__r2fs[rule] = fs
			return fs

class TreeGen:
	def __init__(self):
		self.min_branch_len = 5
		self.max_branch_len = 8
		self.preserve_prob = 0.3
		self.min_pred_change = 1
		self.max_preds = 3
		self.predicates = [chr(i) for i in range(ord('A'), ord('Z'))]
		self.actions = ['l', 'r']

		self.__states = set()

	def generate(self):
		initial_state = frozenset([random.choice(self.predicates) for i in range(self.max_preds)])
		self.__states = set()
		return self.__generate_rec(initial_state, 0)

	def __generate_rec(self, curr, branch_len):
		self.__states.add(curr)
		if self.max_branch_len == self.min_branch_len:
			if branch_len == self.max_branch_len:
				return State(frozenset(), {})
		elif random.random() < max(0, branch_len - self.min_branch_len) / float(self.max_branch_len - self.min_branch_len):
			return State(frozenset(), {})

		children = {}
		for a in self.actions:
			#pdb.set_trace()
			preserved = []
			# randomly preserve some of the state variables
			for p in curr:
				if random.random() < self.preserve_prob:
					preserved.append(p)

			while self.max_preds - len(preserved) < self.min_pred_change:
				preserved.pop(random.randint(0, len(preserved)-1))

			# keep looping until we get a unique state
			while True:
				new_preds = [random.choice(self.predicates) for i in range(self.max_preds - len(preserved))]
				next_state = frozenset(preserved + new_preds)
				if next_state not in self.__states and len(next_state - curr) >= self.min_pred_change:
					break

			children[a] = self.__generate_rec(next_state, branch_len + 1)
		
		return State(curr, children)

def match_rules_to_states(rules, states):
	for s in states:
		if not s.implemented_by(rules):
			return False
	return True

def make_kif(rules, initial_state):
	s = """
(<= terminal (true goal_achieved))
(<= (goal player 100) (true goal_achieved))
(role player)
(legal player l)
(legal player r)
"""
	
	for init in initial_state:
		s += "(init %s)\n" % init

	for r in rules:
		s += r.get_kif()
	
	return s

def extract_common(r1, r2, states, rules2fstates, modify_origs):
	if r1.is_goal_rule() or r2.is_goal_rule():
		return None
	if r1.get_action() != r2.get_action():
		return None

	orig_fstates = rules2fstates[r1] + rules2fstates[r2]
	pcs_int, ncs_int = r1.lhs_intersect(r2)
	rhs_int = r1.rhs_intersect(r2)
	if len(pcs_int) > 0 and len(rhs_int) > 0:
		cr = Rule(pcs_int, ncs_int, r1.get_action(), rhs_int, "common rule extracted from %s and %s" % (str(r1), str(r2)))
		consistent = True
		for s in states:
			if not s.consistent_with(cr):
				# first try to change lhs
				if not cr.restrict_firing(orig_fstates, [s]):
					# since that didn't work, try removing some rhs 
					# predicates
					cr.set_rhs_intersect(s.get_child(cr.get_action()).preds)
					if len(cr.get_rhs()) == 0:
						return None

		# common rule is acceptable
		if modify_origs:
			for x in [r1, r2]:
				r1.remove_rhs(cr.get_rhs())
		return cr
	
	return None


def extract_all_commons(rules, states, rules2fstates):
	commons = []
	to_remove = set()
	non_goal_rules = filter(lambda r: not r.is_goal_rule(), rules)
	i = 0
	while i < len(non_goal_rules):
		r1 = non_goal_rules[i]
		r1_del = False
		j = i + 1
		while j < len(non_goal_rules):
			r2 = non_goal_rules[j]
			r2_del = False
			cr = extract_common(r1, r2, states, rules2fstates, True)
			if cr != None:
				commons.append(cr)
				if r1.is_goal_rule():
					rules.remove(r1)
					del non_goal_rules[i]
					r1_del = True
				if r2.is_goal_rule():
					rules.remove(r2)
					del non_goal_rules[j]
					r2_del = True
			if r1_del:
				# don't loop on r1 anymore
				break
			elif not r2_del:
				j += 1

		if not r1_del:
			i += 1
	
	for r in to_remove:
		rules.remove(r)

	rules.extend(commons)

def cross_product(list1, list2):
	return reduce(lambda x,y: x+y, ([e1 + e2 for e2 in list2] for e1 in list1))

def split_rule(rule, states, rules2fstates):
	""" Try to split a rule into two rules while maintaining semantics """
	assert len(rule.get_rhs()) > 1, "Resulting rules must have some actions"

	pcs = list(rule.get_pconds())
	ncs = list(rule.get_nconds())
	rhs = list(rule.get_rhs())
	pcs_splits = [(i,) for i in range(len(pcs))] ; random.shuffle(pcs_splits)
	ncs_splits = [(i,) for i in range(len(ncs))] ; random.shuffle(ncs_splits)
	rhs_splits = [(i,) for i in range(len(rhs))] ; random.shuffle(rhs_splits)

	all_split_combs = cross_product(cross_product(pcs_splits, ncs_splits), rhs_splits)
	for pcs_split, ncs_split, rhs_split in all_split_combs:
		if rule.has_pconds():
			pcs1 = pcs[0:pcs_split]
			pcs2 = pcs[pcs_split:]
		else:
			pcs1 = []
			pcs2 = []

		if rule.has_nconds():
			ncs1 = ncs[0:ncs_split]
			ncs2 = ncs[ncs_split:]
		else:
			ncs1 = []
			ncs2 = []

		rhs1 = rhs[0:rhs_split]
		rhs2 = rhs[rhs_split:]

		a = rule.get_action()
		
		r1 = Rule(pcs1, ncs1, a, rhs1, 'split from %s' % rule)
		r2 = Rule(pcs2, ncs2, a, rhs2, 'split from %s' % rule)

		# make sure split rules aren't over-general
		firing_states = rules2fstates[rule]
		other_states = states.difference(firing_states)
		if r1.restrict_firing(firing_states, other_states) and \
				r2.restrict_firing(firing_states, other_states):
			return (r1, r2)

	return None

if __name__ == '__main__':
	random.seed(0)

	tree_gen = TreeGen()
	root = tree_gen.generate()
	all_states = []
	root.get_all_states(all_states)

	graph = open('game.gdl', 'w')
	graph.write("digraph g {\n");
	graph.write(root.get_graphviz())
	graph.write("}\n")
	graph.close()

	rules = []
	root.make_max_rules(rules, frozenset(tree_gen.predicates))
	rules2fstates = Rules2FS(all_states)

	#extract_all_commons(rules, all_states, rules2fstates)

	kif = make_kif(rules, root.preds)

	rule_file = open('rules.kif', 'w')
	rule_file.write(kif)
	rule_file.close()

	for r in rules:
		print r
	
	#print match_rules_to_states(rules, all_states)

	from rule_graph import make_rule_graph

	make_rule_graph(rules)