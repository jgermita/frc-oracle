import java.util.ArrayList;
import java.util.Collections;

public class SimulatedEvent {

	private ArrayList<TeamAtEvent> teams = new ArrayList<TeamAtEvent>();
	private ArrayList<MatchData> matches = new ArrayList<MatchData>();

	double rand = 0.0;

	public SimulatedEvent(ArrayList<TeamData> teamsIn,
			ArrayList<MatchData> matches, double rand) {

		this.rand = rand;
		for (TeamData t : teamsIn) {
			this.teams.add(new TeamAtEvent(t));
		}
		this.matches = matches;
	}

	public ArrayList<TeamAtEvent> getRankings() {
		Collections.sort(this.teams);
		return teams;
	}

	public void playMatches() {
		for(MatchData m : matches) {

			double blueRP = 0;
			double redRP = 0;

			if (!m.isPlayed()) {

			double bav = (m.b1.RP_PER_MATCH + m.b2.RP_PER_MATCH + m.b3.RP_PER_MATCH) / 3.0;
			double rav = (m.r1.RP_PER_MATCH + m.r2.RP_PER_MATCH + m.r3.RP_PER_MATCH) / 3.0;

			bav *= (1.0 + (Util.rand2() * rand * 1.5));
			rav *= (1.0 + (Util.rand2() * rand * 1.5));

			double bo = (m.b1.OPR + m.b2.OPR + m.b3.OPR);
			double ro = (m.r1.OPR + m.r2.OPR + m.r3.OPR);

			bo *= (1.0 + (Util.rand2() * rand));
			ro *= (1.0 + (Util.rand2() * rand));

			blueRP = bav;
			redRP = rav;

			double subtract = 2.0;
			double breach = 40.0;
			double capture = 75.0;
			if (bo > ro) {
				if (ro > breach) {
					subtract = 1.0;
				} else if (ro > capture) {
					subtract = 0.0;
				}

				redRP = Math.max(redRP - subtract, 0.0);

			} else {
				if (bo > breach) {
					subtract = 1.0;
				} else if (bo > capture) {
					subtract = 0.0;
				}

				blueRP = Math.max(blueRP - subtract, 0.0);
			}

			} else {
				String s = m.getRP();

				blueRP = Double.parseDouble(s.split(",")[0]);
				redRP = Double.parseDouble(s.split(",")[1]);
			}

			assignRp(m.b1, blueRP);
			assignRp(m.b2, blueRP);
			assignRp(m.b3, blueRP);
			assignRp(m.r1, redRP);
			assignRp(m.r2, redRP);
			assignRp(m.r3, redRP);
		}
	}

	public void assignRp(TeamData team, double rp) {
		for (TeamAtEvent t : teams) {
			if (t.t.NUMBER == team.NUMBER) {
				t.addRp(rp);
			}
		}
	}


	public class TeamAtEvent implements Comparable {
		TeamData t = null;

		public double rp = 0.0;

		public TeamAtEvent(TeamData t) {
			this.t = t;
		}

		public void addRp(double in) {
			rp += in;
		}

		@Override
		public int compareTo(Object o) {
			TeamAtEvent t1 = (TeamAtEvent) o;

			if (t1.rp > rp) {
				return 1;
			}
			return -1;
		}

		public String toString() {
			return Integer.toString(t.NUMBER) + ", " + this.rp;
		}

	}

}
