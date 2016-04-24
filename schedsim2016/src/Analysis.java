import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import org.apache.commons.math3.stat.StatUtils;


public class Analysis {
	
	ArrayList<AnalyzedTeam> teams = new ArrayList<AnalyzedTeam>();
	
	public Analysis(ArrayList<TeamData> teamsIn) {
		for (TeamData t : teamsIn) {
			this.teams.add(new AnalyzedTeam(t));
		}

	}
	
	public void update(ArrayList<SimulatedEvent.TeamAtEvent> rankings) {

		int c = 0;

		for (SimulatedEvent.TeamAtEvent t : rankings) {
			getTeam(t.t).update(rankings.indexOf(t) + 1.0);

			c++;
		}
		Collections.sort(teams);
	}
	
	public AnalyzedTeam getTeam(TeamData t) {
		for (AnalyzedTeam at : teams) {
			if (at.t.NUMBER == t.NUMBER) {
				return at;
			}
		}
		return null;
	}

	public class AnalyzedTeam implements Comparable {

		TeamData t = null;

		int count = 0;
		double rankSum = 0.0;
		int rankMin = 1;
		int rankMax = 1000;

		int rankAct = 0;

		double rankAv = 0.0;

		double confidence = 0.0;

		public AnalyzedTeam(TeamData t) {
			this.t = t;

			String r[] = SchedSim.rankings.split("\n");

			String number = Integer.toString(t.NUMBER);

			for (String s : r) {

				String data[] = s.split(",");

				// System.out.println("l: " + data.length);
				
				if (data.length <= 1) {
					continue;
				}

				// System.out.println(s);

				if (data[1].equals(number)) {
					this.rankAct = Integer.parseInt(data[0]);
				}
			}

		}

		private Vector<Double> ranks = new Vector<Double>();

		public void update(double newRank) {
			ranks.add(new Double(newRank));

			if (newRank < rankMax)
				rankMax = (int) newRank;
			if (newRank > rankMin)
				rankMin = (int) newRank;

			count++;
			rankSum += newRank;

			rankAv = rankSum / count;

		}

		public int getMode() {
			double data[] = new double[ranks.size()];

			for (int i = 0; i < ranks.size(); i++) {
				data[i] = ranks.get(i).doubleValue();
			}

			return (int) StatUtils.min(StatUtils.mode(data));
		}

		public double getAverage() {
			return rankAv;
		}

		@Override
		public int compareTo(Object o) {
			AnalyzedTeam t = (AnalyzedTeam) o;
			if (t.getAverage() > this.getAverage()) {
				return -1;
			}
			return 1;
		}

		@Override
		public String toString() {
			DecimalFormat df = new DecimalFormat("#.###");
			return t.NUMBER + ", " + rankAct + ", " + df.format(rankAv)
					+ ", " + rankMin + ", "
 + rankMax + ", " + getMode();
		}

	}
}
