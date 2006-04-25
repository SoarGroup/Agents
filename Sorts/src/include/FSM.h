#ifndef FSM_H
#define FSM_H

#include<map>
#include<string>
#include<list>
#include<vector>

#include "general.h"
#include "GameObj.H"
#include "SoarAction.h"

class Sorts;

class FSM{
 public:
	FSM(const Sorts *, GameObj *);
	virtual ~FSM();

	virtual int update()=0;

	virtual GameObj *getGameObject(){return gob;}
	virtual void init(std::vector<sint4>);
	virtual ObjectActionType getName();

protected:
	ObjectActionType name;
	GameObj *gob;
	Vector<sint4> params;
	const Sorts *sorts;
};


#define FSM_RUNNING 0
#define FSM_SUCCESS 1
#define FSM_FAILURE 2
#define FSM_STUCK 3

#endif
