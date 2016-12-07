package fun.personalAcademics.primeFactorizor;

import java.util.Random;

public class TestMe {

	public static void main(String[] args) {
		System.out.println(PrimeFactorizor.NUMBER_OF_PRIMES_IN_FILE);
		System.out.println(PrimeFactorizor.MAX_PRIME_FACTOR.toString());
		
//		Random rand = new Random();
//		long randomInt = Math.abs(rand.nextInt());
//		System.out.println("Random number: " + randomInt);
//		System.out.println(PrimeFactorizor.getPrimeFactorsToString(randomInt));
//		System.out.printf("Value needed: %f, Largest prime: %d\n", 
//				Math.sqrt(Long.MAX_VALUE), PrimeFactorizor.MAX_PRIME_FACTOR);

	}
	
	private static long nextRandom(Random random){
		long value = Math.abs(random.nextLong());
		while(value > PrimeFactorizor.MAX_FACTORABLE_NUMBER.doubleValue()){
			value = Math.abs(random.nextLong());
		}
		
		return value;
	}

}
