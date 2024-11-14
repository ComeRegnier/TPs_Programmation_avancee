public class Main {
	public static void main(String[] args) {
		semaphoreBinaire semaphore = new semaphoreBinaire(1);

		Thread taskA = new Thread(() -> {
			try {
				semaphore.syncWait();
				System.out.println("section critique A");
				new Affichage("AAA").run();
				System.out.println("\nsection critique A");
			} finally {
				semaphore.syncSignal();
			}
		});

		Thread taskB = new Thread(() -> {
			try {
				semaphore.syncWait();
				System.out.println("section critique B");
				new Affichage("BB").run();
				System.out.println("\nsection critique B");
			} finally {
				semaphore.syncSignal();
			}
		});

		taskA.start();
		taskB.start();
	}
}
