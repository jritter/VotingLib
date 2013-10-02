package ch.bfh.evoting.votinglib.util;

import java.io.Serializable;
import java.util.Comparator;

import ch.bfh.evoting.votinglib.entities.Option;

public class OptionsComparator implements Comparator<Option>, Serializable{

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Option op1, Option op2) {
		//return the inverse of what is specified in the doc to have
		//an inverted order (descending) when calling Collections.sort()
		if(op1.getVotes()>op2.getVotes()) return -1;
		else if (op1.getVotes()>op2.getVotes()) return 1;
		else return 0;
	}

}
