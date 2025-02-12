state_action(standing,look).
state_action(found1,kill).
state_action(found2orMore,deceive).
state_action(goalAccomplished,move).
state_action(notFound,move).

substate_action(advantageReceived,report).
substate_action(reported,dontVote).

//first action: each impostor introduce itself to the rest of the group, pretending to be a crewmate
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
	-newSubState(S2)[source(percept)];
	.findall(A,substate_action(S2,A),L);
	.nth(0,L,Action);
	+chosenAction(Action,N).

//implementation of each action
+chosenAction(look) <-
	-chosenAction(look);
	searchCrewmatesInRoom.

+chosenAction(kill) : involving(P1)[source(percept)] <-
	-chosenAction(kill);
	-involving(P1)[source(percept)];
	kill(P1).

+chosenAction(deceive) <-
	-chosenAction(deceive);
	deceive.

+chosenAction(move) <-
	-chosenAction(move);
	!nextRoom.

+chosenAction(report,N) : involving(P1,N) <-
	print("I saw ", P1, " killing a crewmate");
	reportImpostor(P1,X).

+chosenAction(dontVote,N) : involving(P1,P2,N) <-
	print("Sorry ", P2, ", I'm not totally sure ", P1, " is an impostor");
	removeTrust(P1,P2).

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
