//assigns a color to a player
+assignColor(A,X) <- .send(A,tell,myColor(X)).
//assigns a initial room to a player
+assignInitialRoom(A,X) <- .send(A,tell,initialRoom(X)).
//assigns to a crewmate its level of skill
+assignLevelOfSkill(A,X) <- .send(A,tell,mySkill(X)).
//assigns to an impostor its behavioral pattern
+assignBehavior(A,X) <- .send(A,tell,myBehavior(X)).

//expulsion action, removes one player from the spaceship
+expulsion(A) <- .send(A,tell,expelled).

//possible win situations, triggered by the environment
+announceVictory(impostors) : not finished <-
	print("The spaceship has been taken over and now belongs to impostors");
	+finished;
	.broadcast(tell,endGame).

+announceVictory(crewmates1) : not finished <-
	print("All of the machinery were fixed, the central computer is now fully operating and has expelled the impostors");
	+finished;
	.broadcast(tell,endGame).

+announceVictory(crewmates2) : not finished <-
	print("All the impostors have been expelled by crewmates");
	+finished;
	.broadcast(tell,endGame).
