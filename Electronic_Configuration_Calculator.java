import java.util.ArrayList;

class Electronic_Configuration_Calculator
{
	public final char[] l =	{'s','p','d','f','g','h','i','k','l','m','n','o','q','r','t','u','v','w','x','y','z'};
	
	public ArrayList<String> generateSubshells() // optimized
	{
		ArrayList<String> subshells = new ArrayList<>();

		// building pattern s-s ps-ps dps-dps fdps-fdps gfdps-gfdps hgfdps-hgfdps...
		for(int i = 0; i < l.length; i++)
			for (int r = 0; r < 2; r++) // repeat twice lol
				for(int j = i; j >= 0; j--) subshells.add(String.valueOf(l[j]));

		// numbering each orbital type as 1s, 2s, 2p, 3s, 3p, 4s, 3d, 4p, 5s, 4d, 5p, 6s, 4f, 5d, 6p, 7s, 5f, 6d, 7p...
		for(int orbital = 0; orbital < l.length; orbital++)
			for(int pos = 0, shell = orbital + 1; pos < subshells.size(); pos++)
				if(subshells.get(pos).charAt(0) == l[orbital])
					subshells.set(pos, shell++ + subshells.get(pos));

		return subshells;
	}

	int getCapacity(char orbitalType)
	{
		for (int i = 0; i < l.length; i++)
			if (l[i] == orbitalType)
				return 4 * i + 2; // s can have 2 electrons, p can have 6, d - 10, f - 14, g - 18, and so on...
				return 2; // default
	}

	public String getConfig(int atomicNumber, String divider)
	{
		if (atomicNumber <= 0) return "Invalid Atomic Number"; 
		int remainingElectrons = atomicNumber;
		String eConfig = "";
		ArrayList<String> subshells = generateSubshells();
		for (String subshell : subshells)
		{
			if (remainingElectrons <= 0) break;
			char orbitalType = subshell.charAt(subshell.length() - 1);
			int maxCapacity = getCapacity(orbitalType);
			int electronsToPlace = Math.min(remainingElectrons, maxCapacity);
			eConfig += subshell + electronsToPlace + divider;
			remainingElectrons -= electronsToPlace;
		}
		if (remainingElectrons > 0)
			eConfig += "... [Overflow: " + remainingElectrons + " electrons remaining.]";
		return eConfig.trim();
	}
}