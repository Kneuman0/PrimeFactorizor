package fun.personalAcademics.primeFactorizor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiFunction;

public class PrimeFactorizor {
	private static BigInteger maxPrime = null;
	private static long numbers = 0;
	static{
		try(FileReader reader = new FileReader(
				PrimeFactorizor.class.getResource("/resources/primes.txt").toString().replace("file:/", ""));
				Scanner scanner = new Scanner(reader)){
			long counter = 0;
			while(scanner.hasNext()){
				counter++;
				String number = scanner.nextLine();
				if(!scanner.hasNext()){
					maxPrime = new BigInteger(number);
				}
			}			
			numbers = counter;
			
		}catch(IOException e){
			e.printStackTrace();
		}
//		try (ReversedLinesFileReader reverseReader = 
//				new ReversedLinesFileReader(new File("primes.txt"), Charset.forName("UTF-8"));){
//			maxPrime = new BigInteger(reverseReader.readLine());
//		} catch (IOException e) {
//			generatePrimesText();
//			try (ReversedLinesFileReader reverseReader = 
//					new ReversedLinesFileReader(new File("primes.txt"), Charset.forName("UTF-8"));){
//				maxPrime = new BigInteger(reverseReader.readLine());
//			}catch(IOException error){
//				e.printStackTrace();
//			}
//		}
	}
	public static final long NUMBER_OF_PRIMES_IN_FILE = numbers;
	public static final BigInteger MAX_PRIME_FACTOR = maxPrime;
	public static final BigInteger MAX_FACTORABLE_NUMBER = MAX_PRIME_FACTOR.pow(2);
	
	public PrimeFactorizor() {
		// nothing to initialize right now
	}
	
	public static Map<Long, Long> getPrimeFactors(long number) throws ArithmeticException{
		if(number <= 1 || MAX_FACTORABLE_NUMBER.compareTo(new BigInteger(Long.toString(number))) < 0){
			throw new ArithmeticException(
					String.format("Value must be > than 1 and < %s, value: %d found", 
							MAX_FACTORABLE_NUMBER.toString(), number));
		}
		
		Scanner reader = null;
		try {
			reader = new Scanner(new FileReader("primes.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<Long, Long> primeFactors = new HashMap<Long, Long>();
		long rightFactor = number;
		BiFunction<Long, Long, Long> biFunction = (Factor, Occurence) -> {
			if(Occurence == null){
				return primeFactors.put(Factor, new Long(1));
			}else{
				return Occurence + 1;
			}
			
		};

		while(rightFactor != 1){
			long nextPrime = Long.parseLong(reader.nextLine());
			while(rightFactor % nextPrime == 0){
				primeFactors.compute(nextPrime, biFunction);
				rightFactor /= nextPrime;
			}
						
			if(!reader.hasNext()){
				reader.close();
				return getPrimeFactors(rightFactor, nextPrime, primeFactors);
			}
		}	
		
		reader.close();
		
		return primeFactors;
	}
	
	public static Map<Long, Long> getPrimeFactors(long number, long lastPrime, Map<Long, Long> primeFactors){
		if(number <= 1){
			throw new ArithmeticException(
					String.format("Value must be > than 1, value: %d found", number));
		}
		
		long rightFactor = number;
		BiFunction<Long, Long, Long> biFunction = (Factor, Occurence) -> {
			if(Occurence == null){
				return primeFactors.put(Factor, new Long(1));
			}else{
				return Occurence + 1;
			}
			
		};

		long[] numbers = getNumberLineArray(1);
		long[] primes = processMiniSieve1st(Arrays.copyOf(numbers, numbers.length));
		long lastNumber = 0;
		int index = -1;
		while(rightFactor != 1){
			
			index = getNextPrimeIndexInNumberLine(primes, index);
			if(index == -1){
				lastNumber = numbers[numbers.length - 1];
				numbers = getNumberLineArray(lastNumber);
				primes = processMiniSieveAfter1st(Arrays.copyOf(numbers, numbers.length));
				index = getNextPrimeIndexInNumberLine(primes, -1);	
				System.out.printf("Searching between %d and %d\n",lastNumber, numbers[numbers.length - 1]);
			}
			long nextPrime = primes[index];
			while(rightFactor % nextPrime == 0){
				primeFactors.compute(nextPrime, biFunction);
				rightFactor /= nextPrime;
			}
		}	
				
		return primeFactors;
	}
		
	private static long[] getNumberLineArray(long startNumber){
		long[] temp = new long[10000000];
		long index = startNumber;
		index++;
		for(int i = 0; i < temp.length; i++){
			temp[i] = i + index;			
		}
		return temp;
	}
	
	public static String getPrimeFactorsToString(long number){
		Map<Long, Long> primeFactors = getPrimeFactors(number);
		Iterator<Long> itr = primeFactors.keySet().iterator();
		String toString = "{";
		while(itr.hasNext()){
			Long factor = itr.next();
			toString += String.format("%d^%d, ", factor, primeFactors.get(factor));
		}
		toString = toString.substring(0, toString.length()-2) + "}";
		
		return toString;
	}
	
	private static int getNextPrimeIndexInNumberLine(long[] numbers, int lastIndex){
		int noMorePrimes = -1;
		for(int i = lastIndex + 1; i < numbers.length; i++){
			if(numbers[i] != 0){
				return i;
			}
		}
		
		return noMorePrimes;
	}
	
	
	public static void generatePrimesText(){
			
		long[] numbers = getNumberLineArray(1);
		long biggestPrime = (long)Math.sqrt(Long.MAX_VALUE);
		System.out.printf("Finding primes between: %d and %d\n", numbers[0], numbers[numbers.length - 1]);
		long lastNumber = numbers[numbers.length - 1];
		long[] primes = processMiniSieve1st(numbers);
		printMiniSieve(primes);
		while(lastNumber < biggestPrime && lastNumber > 0){
			numbers = getNumberLineArray(lastNumber);
			System.out.printf("Finding primes between: %d and %d. %d iterations left\n",
					numbers[0], numbers[numbers.length - 1], (long)((biggestPrime - lastNumber)/10000000));
			lastNumber = numbers[numbers.length - 1];
			primes = processMiniSieveAfter1st(numbers);
			printMiniSieve(primes);
		}
	}
	
	private static long[] processMiniSieveAfter1st(long[] mini){
		long miniArrayValue = mini[0];
		for(long i = 2; i < miniArrayValue; i++){
			int increment = i > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)i;
			for(int index = checkForZeroIndex(miniArrayValue, increment); index < mini.length; index+=increment){
				mini[index] = 0;
			}
		}
		
		return mini;
	}
	
	private static int checkForZeroIndex(long miniArrayValue, int increment){
		if(miniArrayValue % increment == 0){
			return 0;
		}else{
			return increment - (int)(miniArrayValue % increment);
		}
	}
	
	private static int getNextPrimeIndex(long[] mini, int lastIndex){
		int noMorePrimes = -1;
		for(int i = lastIndex; i < mini.length; i++){
			if(mini[i] != 0){
				return i;
			}
		}
		
		return noMorePrimes;
	}
	
	private static long[] processMiniSieve1st(long[] mini){
		long lastNumber = mini[mini.length - 1];
		for(long i = 0; i < lastNumber; i++){
			int nextPrimeIndex = getNextPrimeIndex(mini, (int)i);
			i = nextPrimeIndex;
			if(nextPrimeIndex == -1) break;
			int increment = (int)mini[nextPrimeIndex];
			for(int index = nextPrimeIndex; index < mini.length; index += increment){
				if(index != nextPrimeIndex){
					mini[index] = 0;
				}				
			}
		}
		
		return mini;
	}
	
	private static void printMiniSieve(long[] mini){
		
		try(FileWriter file = new FileWriter("primes.txt", true);
				PrintWriter out = new PrintWriter(file);){
			for(long prime : mini){
				if(prime != 0){
					out.println(prime);
				}
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public static void addToPrimesText(){
		try(FileWriter file = new FileWriter("primes.txt", true);
				PrintWriter fileWriter = new PrintWriter(file);){
			BigInteger nextPrime = new BigInteger(MAX_PRIME_FACTOR.toString());
			while(nextPrime.compareTo(new BigInteger("3037000599")) < 0){
				nextPrime = nextPrime.nextProbablePrime();
				fileWriter.println(nextPrime.toString());
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void deleteLastPrime(){
		try (FileWriter reader = new FileWriter("primesTemp.txt");
				PrintWriter writer = new PrintWriter(reader);
				FileReader fileIn = new FileReader("primes.txt");
				Scanner scanner = new Scanner(fileIn);){
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				if(scanner.hasNextLine()){
					writer.println(line);
				}
			}
						
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String toStringStatic(){
		return MAX_PRIME_FACTOR.toString();
	}
}
