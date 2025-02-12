//first action: each impostor introduce itself to the rest of the group, pretending to be a crewmate
+myColor(X) <-
	print("I'm the ", X, " spaceman").

//receiving the initial room by the game system
+initialRoom(A)[source(gameManager)] <-
	+myRoom(A);
	-initialRoom(A)[source(gameManager)].

//when entering a new room, check if there is a crewmate in the room
+myRoom(A) <-
	searchCrewmatesInRoom.
			
//if there is no crewmate in the current room, move to another room
+searchOutcome(notFound,0)[source(percept)] : not expelled <-
	-searchOutcome(notFound,0)[source(percept)];
	!nextRoom.

//if there is at least one crewmate in the room, the action will depend by the behavior of the impostor:

//first scenario: methodical impostor, kills the found crewmate if there is only one crewmate in the same
//				  room or chooses randomly among kill or deceive if there are 2 or more crewmates in the room
+searchOutcome(A,N)[source(percept)] : myBehavior(methodical) & not expelled <-
	-searchOutcome(A,N)[source(percept)];
	if(N >= 2) {
		.random(R);
		if (R < 0.3) {
			deceive;
		} else {
			kill(A);
		}
	} else {
		kill(A);
	}
	!nextRoom.
													
//second scenario: standard impostor, choses randomly if kill or deceive crewmates not considering the
//				   number of crewmates in the room
+searchOutcome(A,N)[source(percept)] : myBehavior(standard) & not expelled <-
	-searchOutcome(A,N)[source(percept)];
	.random(R);
	if (R < 0.3) {
		deceive;
	} else {
		kill(A);
	}
	!nextRoom.
													
//third scenario: killer impostor, kills one of the crewmates in the room without considering the possibility
//				  of deceiving crewmates as well as the number of crewmates in the room
+searchOutcome(A,N)[source(percept)] : myBehavior(killer) & not expelled <-
	-searchOutcome(A,N)[source(percept)];
	kill(A);
	!nextRoom.

//if the impostor is discovered by a cremate and gets the advantage of speaking first, the impostor reports the crewmate to be an impostor
+advantageReceivedOn(A)[source(percept)] : myColor(X) & not reported(A) & not expelled <-
	print("I saw ", A, " killing a crewmate");
	-advantageReceivedOn(A)[source(percept)];
	+reported(A);
	reportImpostor(A,X).

//in case of a dispute, the impostor will never risk to get unmasked by defending another player
+reportedImpostor(A,B,N) : myColor(X) & not (A==X) <-
	print("Sorry ", B, ", I'm not totally sure ", A, " is an impostor").
							
//clears the unnecessary beliefs when the game ends
+endGame <- .abolish(reportedImpostor(_,_,_)).

//the next room the impostor will visit is based on their current room
+!nextRoom : myRoom(section_1) <- moveToNextRoom(section_2); -myRoom(section_1)[source(_)]; +myRoom(section_2).
+!nextRoom : myRoom(section_2) <- moveToNextRoom(section_3); -myRoom(section_2)[source(_)]; +myRoom(section_3).
+!nextRoom : myRoom(section_3) <- moveToNextRoom(section_4); -myRoom(section_3)[source(_)]; +myRoom(section_4).
+!nextRoom : myRoom(section_4) <- moveToNextRoom(section_5); -myRoom(section_4)[source(_)]; +myRoom(section_5).
+!nextRoom : myRoom(section_5) <- moveToNextRoom(section_1); -myRoom(section_5)[source(_)]; +myRoom(section_1).
//handling plan failure
-!nextRoom <- !nextRoom.
