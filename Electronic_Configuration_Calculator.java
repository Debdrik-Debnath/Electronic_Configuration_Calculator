import java.util.ArrayList;

class Electronic_Configuration_Calculator
{
	public final char[] l = {'s','p','d','f','g','h','i','k','l','m','n','o','q','r','t','u','v','w','x','y','z'};
	private ArrayList<String> cachedSubshells;

	// # precompute the subshells once for efficiency
	public Electronic_Configuration_Calculator() {this.cachedSubshells = generateSubshells();}

	public ArrayList<String> generateSubshells()
	{
		ArrayList<String> subshells = new ArrayList<>();

		// building pattern s-s ps-ps dps-dps...
		for(int i = 0; i < l.length; i++)
			for (int r = 0; r < 2; r++) 
				for(int j = i; j >= 0; j--)
					subshells.add(String.valueOf(l[j]));   
		
		// numbering each orbital type safely using endsWith
		for(int orbital = 0; orbital < l.length; orbital++)
		{
			int shell = orbital + 1;
			for(int pos = 0; pos < subshells.size(); pos++) 
				if(subshells.get(pos).endsWith(String.valueOf(l[orbital])))
					subshells.set(pos, shell++ + subshells.get(pos));
		}
		return subshells;
	}

	int getCapacity(char orbitalType)
	{
		for (int i = 0; i < l.length; i++)
			if (l[i] == orbitalType)
				return 4 * i + 2;
				return 2; 
	}

	public String getConfig(int atomicNumber, String divider)
	{
		if (atomicNumber <= 0) return "Invalid Atomic Number";
		int remainingElectrons = atomicNumber;
		
		// parallel arrays to keep track of filled electrons per subshell
		int size = cachedSubshells.size();
		int[] allocated = new int[size];
		int sOrbitalIndex = -1; // tracks the active ns orbital index for borrowing

		// Step 1: Standard Aufbau Distribution
		for (int i = 0; i < size; i++)
		{
			if (remainingElectrons <= 0) break;

			String subshell = cachedSubshells.get(i);
			char orbitalType = subshell.charAt(subshell.length() - 1);
			int maxCapacity = getCapacity(orbitalType);
			int electronsToPlace = Math.min(remainingElectrons, maxCapacity);

			allocated[i] = electronsToPlace;
			remainingElectrons -= electronsToPlace;

			// track the most recent 's' orbital to borrow from
			if (orbitalType == 's') sOrbitalIndex = i;

			// Step 2: Dynamic Rule Evaluation (check for possibility of stability jumps)
			if (sOrbitalIndex != -1 && (orbitalType == 'd' || orbitalType == 'f'))
			{
				int currentCount = allocated[i];
				int halfFilled = maxCapacity / 2;

				// check if borrowing one electron creates a stable half-filled or full-filled state
				boolean becomesStable = (currentCount + 1 == halfFilled) || (currentCount + 1 == maxCapacity);

				// ensure the preceding s-orbital actually has an electron to give
				boolean canBorrow = allocated[sOrbitalIndex] > 0;

				if (becomesStable && canBorrow)
				{
					allocated[sOrbitalIndex]--;
					allocated[i]++;
				}
			}
		}

		// Final Result
		StringBuilder eConfig = new StringBuilder();
		for (int i = 0; i < size; i++)
			if (allocated[i] > 0)
				eConfig.append(cachedSubshells.get(i)).append(allocated[i]).append(divider);
		if (remainingElectrons > 0)
			eConfig.append("... [Overflow: ").append(remainingElectrons).append(" electrons remaining.]");
		return eConfig.toString().trim();
	}
}
