//first action: each crewmate introduces itself to the rest of the group
+myColor(X) <-
	print("I'm the ", X, " spaceman").

//receiving the initial room by the game system
+initialRoom(A)[source(gameManager)] <-
	+myRoom(A);
	-initialRoom(A)[source(gameManager)].

//when entering a new room, check if there is something to fix in the room
+myRoom(A) <-
	checkRoomSituation.

//if there is something to fix in the room, fix it, then check again if there is something else to do
+checkOutcome(taskDetected)[source(percept)] : mySkill(S) & not expelled <-
	-checkOutcome(taskDetected)[source(percept)];
	.random(R);
	if(R < S) {
		fixMachinery;
	}
	checkRoomSituation.

//if there is nothing to fix in the room, move to another room
+checkOutcome(nothingToDo)[source(percept)] : not expelled <-
	-checkOutcome(nothingToDo)[source(percept)];
	!nextRoom.
				
//if another player is seen solving a task in the same room, that player will be trusted
+wasFixing(A)[source(percept)] : not untrusted(A) <-
	+trusted(A);
	-wasFixing(A)[source(percept)].

//if another player is seen killing in the same room, that player will be untrusted
+wasKilling(A)[source(percept)] <-
	+untrusted(A);
	-trusted(A);
	-wasKilling(A)[source(percept)].

//if the crewmate discovers an impostor and gets the advantage of speaking first, the crewmate reports the impostor
+advantageReceivedOn(A)[source(percept)] : myColor(X) & not reported(A) & not expelled <-
	print("I saw ", A, " killing a crewmate");
	-advantageReceivedOn(A)[source(percept)];
	+reported(A);
	reportImpostor(A,X).

//when a player B reports a player A to be an impostor, if B is trusted or A is untrusted, A is recognized as an impostor
+reportedImpostor(A,B,N) : ((trusted(B) & not trusted(A)) | (untrusted(A) & not untrusted(B))) & myColor(X) & not (A==X) & not(B==X) & not voted(A) & not expelled <-
	print("Answering to ", B,", I agree about ", A, " being an impostor");
	+trusted(B);
	+untrusted(A);
	+voted(A);
	emergencyExpulsion(A).

//if A is trusted or B is untrusted, B it's considered for sure an impostor telling a lie
+reportedImpostor(A,B,N) : ((trusted(A) & not trusted(B)) | (untrusted(B) & not untrusted(A))) & myColor(X) & not (A==X) & not voted(B) & not expelled <- 
	print("I'm sure ", B, " is lying and is an impostor, ", A," is innocent");
	+trusted(A);
	+untrusted(B);
	+voted(B);
	reportImpostor(B,X).

//if both A and B are trusted, both will now be kept in doubt because one of them is an impostor for sure
+reportedImpostor(A,B,N) : trusted(A) & trusted(B) & not untrusted(A) & not untrusted(B) & myColor(X) & not (A==X) & not expelled <- 
	print("Sorry ", B, ", I'm not totally sure ", A, " is an impostor");
	-trusted(A);
	-trusted(B).

//if neither A or B are trusted nor untrusted, both will be kept in doubt
+reportedImpostor(A,B,N) : not trusted(A) & not trusted(B) & not untrusted(A) & not untrusted(B) & myColor(X) & not (A==X) & not expelled <- 
	print("Sorry ", B, ", I'm not totally sure ", A, " is an impostor").

//clears the unnecessary beliefs when the game ends
+endGame <- .abolish(reportedImpostor(_,_,_)).

//the next room the crewmate will visit is based on their current room
+!nextRoom : myRoom(section_1) <- moveToNextRoom(section_2); -myRoom(section_1)[source(_)]; +myRoom(section_2).
+!nextRoom : myRoom(section_2) <- moveToNextRoom(section_3); -myRoom(section_2)[source(_)]; +myRoom(section_3).
+!nextRoom : myRoom(section_3) <- moveToNextRoom(section_4); -myRoom(section_3)[source(_)]; +myRoom(section_4).
+!nextRoom : myRoom(section_4) <- moveToNextRoom(section_5); -myRoom(section_4)[source(_)]; +myRoom(section_5).
+!nextRoom : myRoom(section_5) <- moveToNextRoom(section_1); -myRoom(section_5)[source(_)]; +myRoom(section_1).
//handling plan failure
-!nextRoom <- !nextRoom.