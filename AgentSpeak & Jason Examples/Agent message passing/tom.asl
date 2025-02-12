!start.

+!start : true <- 
	.print("Sending 'hello' to bob");
	.send(bob,tell,hello).

+hello[source(A)] <-
	.print("I received back 'hello' from ",A);
	.send(A,tell,hello).
