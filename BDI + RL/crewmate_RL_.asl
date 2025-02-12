state_action(standing,look).
state_action(taskDetected,repair).
state_action(nothingToDo,move).

substate_action(wasFixing,trust).
substate_action(wasKilling,untrust).
substate_action(reported_trustAccuser,voteForAccused).
substate_action(reported_trustAccused,voteForAccuser).
substate_action(reported_untrustAccuser,voteForAccuser).
substate_action(reported_untrustAccused,voteForAccused).
substate_action(reported_dontKnow,dontVote).
substate_action(advantageReceived,report).

//first action: each crewmate introduces itself to the rest of the group
+myColor(X) <-
	print("I'm the ", X, " spaceman").

//receiving the initial room by the game system
+initialRoom(A)[source(gameManager)] <-
	+myRoom(A);
	+myState(standing);
	-initialRoom(A)[source(gameManager)].

//plan for selecting an action for the new received state
+myState(S1) : not expelled <-
	.findall(A,state_action(S1,A),L);
	.nth(0,L,Action);
	+chosenAction(Action).

//receiving a new state from the environment as a consequence of the executed action
+newState(NS1)[source(percept)] : myState(S1) <-
	-newState(NS1)[source(percept)];
	-myState(S1);
	+myState(NS1).

//plan for selecting an action for the new received substate
+newSubState(S2,N)[source(percept)] : not expelled <-
	-newSubState(S2,N)[source(percept)];
	.findall(A,substate_action(S2,A),L);
	.nth(0,L,Action);
	+chosenAction(Action,N).

//implementation of each action
+chosenAction(look) <-
	-chosenAction(look);
	checkRoomSituation.

+chosenAction(repair) : mySkill(S) <-
	-chosenAction(repair);
	.random(R);
	if(R < S) {
		fixMachinery;
	}.

+chosenAction(move) <-
	-chosenAction(move);
	!nextRoom.

+chosenAction(trust,N) : involving(P1,N)[source(percept)] <-
	trust(P1).

+chosenAction(untrust,N) : involving(P1,N)[source(percept)] <-
	untrust(P1).

+chosenAction(voteForAccuser,N) : involving(P1,P2,N) <-
	print("I'm sure ", P2, " is lying and is an impostor, ", P1," is innocent");
	trust(P1);
	untrust(P2);
	reportImpostor(P2,X).

+chosenAction(voteForAccused,N) : involving(P1,P2,N) <-
	print("Answering to ", P2,", I agree about ", P1, " being an impostor");
	trust(P2);
	untrust(P1);
	emergencyExpulsion(P1).

+chosenAction(dontVote,N) : involving(P1,P2,N) <-
	print("Sorry ", P2, ", I'm not totally sure ", P1, " is an impostor");
	removeTrust(P1,P2).
												
+chosenAction(report,N) : involving(P1,N) <-
	print("I saw ", P1, " killing a crewmate");
	reportImpostor(P1,X).

//clears the unnecessary beliefs when the game ends
+endGame <- .abolish(chosenAction(_,_)); .abolish(involving(_,_,_)); .abolish(involving(_,_)); .abolish(newSubState(_,_)).

//the next room the crewmate will visit is based on their current room
+!nextRoom : myRoom(section_1) <- moveToNextRoom(section_2); -myRoom(section_1)[source(_)]; +myRoom(section_2).
+!nextRoom : myRoom(section_2) <- moveToNextRoom(section_3); -myRoom(section_2)[source(_)]; +myRoom(section_3).
+!nextRoom : myRoom(section_3) <- moveToNextRoom(section_4); -myRoom(section_3)[source(_)]; +myRoom(section_4).
+!nextRoom : myRoom(section_4) <- moveToNextRoom(section_5); -myRoom(section_4)[source(_)]; +myRoom(section_5).
+!nextRoom : myRoom(section_5) <- moveToNextRoom(section_1); -myRoom(section_5)[source(_)]; +myRoom(section_1).
//handling plan failure
-!nextRoom <- !nextRoom.
