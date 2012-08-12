package org.socialsignin.springsocial.security.signin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class which given a collection of elements of
 * type <T> can obtain a list of all possible combinations
 * of those elements.  The list of combinations is ordered
 * in ascending order of the number of elements in each combination.
 * 
 * @author Michael Lavelle
 *
 * @param <T> The type of the elements in each combination
 */
public class CombinationHelper<T> {

	private List<T> valuesList;

	public CombinationHelper(Collection<T> values) {
		this.valuesList = new ArrayList<T>(values);
	}

	private String getCombinationBitmask(int combination) {
		int leading = valuesList.size()
				+ Integer.numberOfLeadingZeros(combination) - 32;
		;
		StringBuffer sb = new StringBuffer();
		for (int padding = 0; padding < leading; padding++) {
			sb.append("0");
		}
		sb.append(Integer.toBinaryString(combination));
		return sb.toString();
	}

	private Set<T> getCombinationForCombinationNumber(int combinationNumber) {
		Set<T> combination = new HashSet<T>();
		String combinationBitmask = getCombinationBitmask(combinationNumber);
		char[] bitMaskChars = combinationBitmask.toCharArray();
		for (int bitMaskCharIndex = 0; bitMaskCharIndex < bitMaskChars.length; bitMaskCharIndex++) {
			if (bitMaskChars[bitMaskCharIndex] == '1') {
				T value = valuesList.get(bitMaskCharIndex);
				if (!combination.contains(value)) {
					combination.add(value);
				}
			}

		}
		return combination;
	}

	public List<Set<T>> getCombinations() {
		int combinationCount = (int) Math.pow(2d, valuesList.size()) - 1;
		List<Set<T>> combinations = new ArrayList<Set<T>>();
		for (int combinationNumber = 1; combinationNumber <= combinationCount; combinationNumber++) {
			combinations
					.add(getCombinationForCombinationNumber(combinationNumber));
		}

		Collections.sort(combinations, new Comparator<Set<T>>() {
			@Override
			public int compare(Set<T> s1, Set<T> s2) {
				return s1.size() - s2.size();
			}
		});
		return combinations;
	}

}
