import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;


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
		double rankMin = 1.0;
		double rankMax = 1000.0;

		double rankAv = 0.0;

		double confidence = 0.0;

		public AnalyzedTeam(TeamData t) {
			this.t = t;
		}

		public void update(double newRank) {
			if (newRank < rankMax)
				rankMax = newRank;
			if (newRank > rankMin)
				rankMin = newRank;

			count++;
			rankSum += newRank;

			rankAv = rankSum / count;
			confidence = 1 - ((rankMin - rankAv) / rankMax);
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
			return t.NUMBER + ", " + df.format(rankAv) + ", " + rankMin + ", "
					+ rankMax + ", " + df.format(confidence);
		}

	}
}
