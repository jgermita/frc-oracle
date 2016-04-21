import java.util.ArrayList;

import com.plnyyanks.tba.apiv2.interfaces.APIv2;
import com.plnyyanks.tba.apiv2.models.Match;

public class MatchData implements Comparable {

	TeamData r1, r2, r3;
	TeamData b1, b2, b3;
	
	int NUMBER = 0;
	private Match m = null;
	private ArrayList<TeamData> teams = null;

	boolean played = false;


	public MatchData(Match m) {

		this.m = m;
		this.NUMBER = (m.getMatch_number());
		handleMatch();
	}
	
	private void handleMatch() {
		APIv2 api = SchedSim.getApi();

		String alliances = m.getAlliances().toString();

		alliances = alliances.replace("{", "");
		alliances = alliances.replace("}", "");
		alliances = alliances.replace("[", "");
		alliances = alliances.replace("]", "");
		alliances = alliances.replace(":", "");
		alliances = alliances.replace("\"", ",");
		alliances = alliances.replace("\"\"", ",");
		alliances = alliances.replace(",,", ",");
		alliances = alliances.replace(",,", ",");
		
		String in[] = alliances.split(",");
		
		String blue[] = { in[5], in[6], in[7], };

		String red[] = { in[12], in[13], in[14], };

		r1 = SchedSim.getTeam(red[0]);
		r2 = SchedSim.getTeam(red[1]);
		r3 = SchedSim.getTeam(red[2]);

		b1 = SchedSim.getTeam(blue[0]);
		b2 = SchedSim.getTeam(blue[1]);
		b3 = SchedSim.getTeam(blue[2]);



		double predRedScore = r1.OPR + r2.OPR + r3.OPR;
		double predBlueScore = b1.OPR + b2.OPR + b3.OPR;

		double redBonus = (r1.RP_PER_MATCH + r2.RP_PER_MATCH + r3.RP_PER_MATCH) / 3.0;
		redBonus = Math.max(redBonus % 2, 0);
		double blueBonus = (b1.RP_PER_MATCH + b2.RP_PER_MATCH + b3.RP_PER_MATCH) / 3.0;
		blueBonus = Math.max(blueBonus % 2, 0);

		played = !m.getScore_breakdown().toString().equals("null");
		/*
		 * System.out.println("Red alliance:");
		 * System.out.println(r1.toString()); System.out.println(r2.toString());
		 * System.out.println(r3.toString());
		 * 
		 * System.out.println("Blue alliance:");
		 * System.out.println(b1.toString()); System.out.println(b2.toString());
		 * System.out.println(b3.toString());
		 * System.out.println("Predicted scores:"); System.out.println("Red: " +
		 * predRedScore + ", " + redBonus + "RP"); System.out.println("Blue: " +
		 * predBlueScore + ", " + blueBonus + "RP");
		 * System.out.println("Winner: " + (predRedScore > predBlueScore ? "Red"
		 * : "Blue"));
		 */
	}


	public boolean isPlayed() {

		// System.out.println(m.getScore_breakdown().toString());

		return played;

	}

	public String getRP() {
		if (!isPlayed()) {
			return "0.0,0.0";
		} else {
			String s = m.getScore_breakdown().toString();
			s = s.replace("{", "");
			s = s.replace("}", "");
			s = s.replace("[", "");
			s = s.replace("]", "");
			s = s.replace(":", "");
			s = s.replace("\"", ",");
			s = s.replace("\"\"", ",");
			s = s.replace(",,", ",");
			s = s.replace(",,", ",");

			String data[] = s.split(",");


			boolean bCap = Boolean.parseBoolean(data[15]);
			boolean bBre = Boolean.parseBoolean(data[71]);

			boolean rCap = Boolean.parseBoolean(data[92]);
			boolean rBre = Boolean.parseBoolean(data[148]);

			boolean rWin = Integer.parseInt(data[110]) > Integer
					.parseInt(data[33]);
			boolean bWin = Integer.parseInt(data[110]) < Integer
					.parseInt(data[33]);
			boolean tie = Integer.parseInt(data[110]) == Integer
					.parseInt(data[33]);

			int bRp = (bCap ? 1 : 0) + (bBre ? 1 : 0)
					+ (tie ? 1 : (bWin ? 2 : 0));
			int rRp = (rCap ? 1 : 0) + (rBre ? 1 : 0)
					+ (tie ? 1 : (rWin ? 2 : 0));


			return bRp + "," + rRp;
		}
	}

	@Override
	public int compareTo(Object o) {
		MatchData m1 = (MatchData) o;

		if (m1.NUMBER < this.NUMBER) {
			return 1;
		}
		return -1;
	}

	public String toString() {
		return Integer.toString(this.NUMBER);
	}

}
