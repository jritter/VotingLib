package ch.bfh.evoting.votinglib.util;

import java.io.Serializable;
import java.util.Comparator;

public class IPAddressComparator implements Comparator<String>, Serializable{

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(String ip1, String ip2) {
		
		String ip1String = ip1.replace(".", "");
		String ip2String = ip2.replace(".", "");
		int ip1Int = Integer.parseInt(ip1String);
		int ip2Int = Integer.parseInt(ip2String);
		return ((Integer)ip1Int).compareTo(ip2Int);
	}

}
