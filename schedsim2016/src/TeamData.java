import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.plnyyanks.tba.apiv2.interfaces.APIv2;
import com.plnyyanks.tba.apiv2.models.Event;
import com.plnyyanks.tba.apiv2.models.Team;

public class TeamData implements Comparable {
	public int NUMBER = 0;

	public double RP_PER_MATCH = 0.0;
	public double OPR = 0.0;

	public Team t = null;
	public String id = null;

	public TeamData(int num) {
		this.NUMBER = num;
		handleTeam(num);

	}

	Event last = null;

	private void handleTeam(int num) {
		String teamId = "frc" + num;
		id = teamId;
		APIv2 api = SchedSim.getApi();

		this.t = api.fetchTeam(teamId, null);

		ArrayList<Event> events = (ArrayList<Event>) api
				.fetchTeamEvents(
				teamId, 2016, null);

		Collections.sort(events);

		last = getMostRecentPlayedEvent(events);
		// System.out.println("Team " + this.NUMBER + " - "
		// + api.fetchTeam(teamId, null).getNickname());

		// System.out.println("Most recent played event is: " + last.getName());
		
		boolean played = false;

		
		if (last != null) {
			played = this.hasPlayedMatches(api.fetchEventRankings(last.getKey(),
					null));	
			this.RP_PER_MATCH = getEventRpPerMatch(api.fetchEventRankings(
				last.getKey(), null));
			this.OPR = getOpr(events, SchedSim.cfg.getOprMode());

		}
		
		// System.out.println("At " + last.getEvent_code() + ", team " +
		// this.NUMBER + " has" + (!played ? " not" : "") +
		// " played matches yet.");

		// System.out.println("Most recent RP/match is: " + this.RP_PER_MATCH);



		// System.out.println("Most recent OPR is: " + this.OPR);

	}

	private Event getMostRecentPlayedEvent(ArrayList<Event> list) {
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar cal = Calendar.getInstance();

		try {
			for (Event e : list) {

				// Really hacky way to handle events that haven't started yet...
				cal.setTime(sdf.parse(e.getStart_date()));
				cal.add(Calendar.DATE, 1); // minus number would decrement the
											// days

				if (cal.getTime().before(today)) {
					// System.out.println("Using " + e.getEvent_code() + " "
					// + e.getStart_date());
					return e;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;

	}
	
	public double getEventRpPerMatch(JsonArray rankings) {

		String in = rankings.toString();

		in = in.replace('{', '\n');
		in = in.replace('}', '\n');
		in = in.replace('[', '\n');
		in = in.replace(']', '\n');
		in = in.replace("\n,\n", "\n");
		in = in.replace("\"", "");
		in = in.replace(
				"\n\nRank,Team,Ranking Score,Auto,Scale/Challenge,Goals,Defense,Record (W-L-T),Played\n",
				"");

		String arr[] = in.split("\n");


		// System.out.println(in);

		double rp = 0.0;

		for (String s : arr) {
			String line[] = s.split(",");

			if (Integer.parseInt(line[1]) == (this.NUMBER)) {
				rp = Double.parseDouble(line[2]) / Double.parseDouble(line[8]);
				break;
			}

		}

		return rp;
	}
	
	public double getEventOpr(JsonObject stats) {

		String in = stats.toString();

		in = in.replace('{', '\n');
		in = in.replace('}', '\n');
		// in = in.replace('[', '\n');
		// in = in.replace(']', '\n');
		in = in.replace("\n,\n", "\n");
		in = in.replace("\"", "");



		String lines[] = in.split("\n");

		String oprs[] = lines[2].split(",");

		double opr = 0.0;

		if (stats == null)
			return 0.0;

		for (String s : oprs) {
			String line[] = s.split(":");

			if (line[0].contains("qual"))
				break;
			if (Integer.parseInt(line[0]) == (this.NUMBER)) {
				opr = Double.parseDouble(line[1]);
			}
		}

		return opr;

	}

	public double getOpr(ArrayList<Event> list, int mode) {

		int c = 0;
		double oprsum = 0.0;
		Calendar cal = Calendar.getInstance();
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		double[] weights = { 2.0, 0.75, 0.50, 0.375, 0.25 };

		WeightedAverage w = new WeightedAverage(weights);
		
		double max = 0.0;

		try {

		for (Event e : list) {

			// Really hacky way to handle events that haven't started yet...
			cal.setTime(sdf.parse(e.getStart_date()));
			cal.add(Calendar.DATE, 1); // minus number would decrement the
										// days

			if (cal.getTime().after(today)) {
				// System.out.println("Using " + e.getEvent_code() + " "
				// + e.getStart_date());
					continue;
			}
			
			double opr = this.getEventOpr(SchedSim.getApi().fetchEventStats(
"2016" + e.getEvent_code(), null));

				if (opr > max)
					max = opr;

				w.add(opr);

		}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(mode == 0) {
			return this.getEventOpr(SchedSim.getApi().fetchEventStats(last.getKey(), null));
		} else if (mode == 1){
			return max;
		} else if (mode == 2) {
			return w.get();
		}

		return 0.0;

	}

	public boolean hasPlayedMatches(JsonArray rankings) {
		// System.out.println("Checking matches played...");

		String in = rankings.toString();

		in = in.replace('{', '\n');
		in = in.replace('}', '\n');
		in = in.replace('[', '\n');
		in = in.replace(']', '\n');
		in = in.replace("\n,\n", "\n");
		in = in.replace("\"", "");
		in = in.replace(
				"\n\nRank,Team,Ranking Score,Auto,Scale/Challenge,Goals,Defense,Record (W-L-T),Played\n",
				"");

		String arr[] = in.split("\n");

		boolean playedMatches = false;

		if (rankings.toString().equals("[]")) {
			return false;
		}

		for (String s : arr) {
			String line[] = s.split(",");


			if (Integer.parseInt(line[1]) == (this.NUMBER)) {
				playedMatches = Integer.parseInt(line[8]) > 0;
				break;
			}

		}


		return playedMatches;
	}
	
	public double getRPPerMatch() {
		return 0.0;
	}

	@Override
	public int compareTo(Object t1) {
		if (this.NUMBER > ((TeamData) t1).NUMBER) {
			return 1;
		} else {
			return -1;
		}
	}

	public String toString() {
		DecimalFormat df = new DecimalFormat("#.###");
		return id + ", " + df.format(this.RP_PER_MATCH) + ", "
				+ df.format(this.OPR);
	}

}
