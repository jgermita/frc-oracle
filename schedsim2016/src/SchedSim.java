import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.plnyyanks.tba.apiv2.APIv2Helper;
import com.plnyyanks.tba.apiv2.interfaces.APIv2;
import com.plnyyanks.tba.apiv2.models.Match;
import com.plnyyanks.tba.apiv2.models.Team;

public class SchedSim {


	private static APIv2 api = null;
	static ArrayList<TeamData> teams = null;

	public static ConfigFile cfg = new ConfigFile("config.txt");

	public static String rankings = "";

	public static APIv2 getApi() {
		if (api == null) {
			APIv2Helper.setAppId("jgermita:schedsim:v0.1");
			api = APIv2Helper.getAPI();
		}

		return api;
	}

	public static void main(String args[]) throws Exception {
		api = getApi();

		String event = "2016pncmp";

		System.out.println("Schedule Simulator 2016");


		// System.out
		// .println("\nNotes: \n"
		// + "\t-Only 2016 event codes are supported\n"
		// + "\t-Data does not exist for teams that have not competed yet\n"
		// + "\t-OPR data is calculated from MOST RECENT event only\n"
		// +
		// "\t-Application requires an active internet connection to run. Verify firewall settings if issues occur.\n"
		// + "\t-Only events with posted match schedules will be simulated.");

		// User input mode:

		boolean loop = true;
		do {
			if (args.length == 0 || args[0].equals("")) {
				System.out
						.println("\nPlease enter an event code. Enter \"help\" for a list of event codes.");
				Scanner scan = new Scanner(System.in);
				String s = scan.nextLine();
				if (s.isEmpty() || s.contains("exit")) {
					break;
				}
				s = s.split("\n")[0];
				
				if (!s.contains("2016")) {
					s = "2016".concat(s);
				}


				s = s.toLowerCase();
				event = s;
			} else {

				if (args[0].equals("?") || args[0].equals("help")) {
					printHelp();

					loop = true;
					break;
				}

				if (args[0].contains("exit")) {
					break;
				}

				loop = false;
				event = "2016" + args[0].toLowerCase();
			}

			if (event.endsWith("help")) {
				printHelp();
				continue;
			}

			rankings = api.fetchEventRankings(event, null).toString();
			rankings = rankings.replace("[", "\n");
			rankings = rankings.replace("]", "\n");
			rankings = rankings.replace("\"", "");
			rankings = rankings.replace(",\n", "");
			rankings = rankings.replace("\n\n", "\n");
			rankings = rankings.replace("\n\n", "\n");

			// System.out.println(rankings);

			System.out.println("\n\nReading data from TBA for: " + event);
			System.out
					.println("This can take up to 2 minutes depending on event size...");
			teams = getTeamList(event);
			System.out.println("Team list parsed... Parsing Matches now...");

			PrintWriter data = new PrintWriter(event + "_data.html");


			Collections.sort(teams);
			System.out.println("\nInput team data: ");
			System.out.println("team, avg RP/Match, opr");
			//data.println("team, avg RP/Match, opr");
			data.println("<table border=\"1\" class=\"sortable\"><TR id=\"headers\"><td>Team<td>Avg Rp/Match<td>OPR<td><br>");
			for (TeamData t : teams) {
				System.out.println(t.toString());
				data.print("<TR id=\"data\"><td>");
				data.println(t.toString().replace(", ", "<td>"));
			}
			data.println("</table></body>");
			data.close();

			System.out.println("\n\n");

			ArrayList<Match> matches = getMatchSchedule(event);
			ArrayList<MatchData> quals = new ArrayList<MatchData>();

			for (Match m : matches) {
				if (m.getComp_level().toString().equals("qm")) {
					quals.add(new MatchData(m));
				}
			}
			Collections.sort(quals);

			System.out
.println("Data received and parsed. Simulating matches "
					+ cfg.getIterations() + " times...");
			System.out.print("Using OPR Mode " + cfg.getOprMode());
			switch (cfg.getOprMode()) {
			case 0:
				System.out.println(" - Most Recent OPR");
				break;
			case 1:
				System.out.println(" - Best OPR");
				break;
			case 2:
				System.out.println(" - Weighted Average of OPRs");
				break;
			default:
				System.out.println(" - NOT RECOGNIZED");
				break;
			}

			System.out.println("Approximate time to complete: "
					+ (cfg.getIterations() / 0.09082 * 0.01)
					+ " seconds");


			SimulatedEvent ev = new SimulatedEvent(teams, quals, 0.25);

			Analysis sim = new Analysis(teams);

			long startTime = System.currentTimeMillis();



			int c = 0;
			for (int i = 0; i < cfg.getIterations(); i++) {
				System.out.print("        \r");
				System.out.print("[");

				ev.playMatches();
				ArrayList<SimulatedEvent.TeamAtEvent> rankings = ev
						.getRankings();
				sim.update(rankings);

				System.out.print(100.0 * ((double) i)
						/ ((double) cfg.getIterations())
						+ "%]");
			}

			PrintWriter pw = new PrintWriter(event + "_predictions.html",
					"UTF-8");

			System.out.println("\nResults: ");
			pw.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\"\\frc-oracle\\tables.css\"><meta name=\"viewport\" content=\"width=device-width, user-scalable=no\"><script src=\"\\frc-oracle\\sorttable.js\"></script></head>");

			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date dateobj = new Date();

			pw.println("<body>Event: " + event + "<br>Last Updated: "
					+ df.format(dateobj) + "<br>");

			pw.println("<table border=\"1\" class=\"sortable\"><TR id=\"headers\"><td>Team<td>Actual Rank<td>Avg Rank<td>Max Rank<td>Min Rank<br>");

			System.out.println("team, avg, max, min, confidence".replace(", ",
					"\t"));

			for (Analysis.AnalyzedTeam t : sim.teams) {
				System.out.println(t.toString().replace(", ", "\t"));
				pw.print("<TR id=\"data\"><td>");
				pw.println(t.toString().replace(", ", "<td>"));
			}

			pw.println("</table></body>");


			pw.close();

			System.out.println("Simulated posted match schedule "
					+ cfg.getIterations() + " times. Time to complete: "
					+ (System.currentTimeMillis() - startTime) * 0.001
					+ " seconds");

			System.out.println("Results have been written to file " + event
					+ "_predictions.csv");


		} while (loop);


	}


	public static ArrayList<TeamData> getTeamList(String eventCode) {
		
		ArrayList<TeamData> answer = new ArrayList<TeamData>();
		
		
		List<Team> list = api.fetchEventTeams(eventCode, null);
		
		int c = 0;

		System.out.print("[");
		for(Team t : list) {
			System.out.print(".");
		}
		System.out.print("] " + list.size() + " Teams\r");
		System.out.print("[");
		for (Team t : list) {
			answer.add(new TeamData((t.getTeam_number())));
			System.out.print("-");
		}
		System.out.println("]");
		
		return answer;
	}

	public static ArrayList<Match> getMatchSchedule(String eventCode) {

		ArrayList<Match> answer = new ArrayList<Match>();

		answer = (ArrayList<Match>) api.fetchEventMatches(eventCode, null);

		return answer;

	}

	public static TeamData getTeam(String id) {
		for (TeamData t : teams) {
			if (t.id.equals(id)) {
				return t;
			}

		}

		System.out.println("team not found!");
		return null;

	}

	public static void printHelp() {

		System.out.println("Code\t\tEvent");
		System.out.println("\nDistrict Championships");
		System.out.println("gacmp\t\tPeachtree District State Championship");
		System.out.println("micmp\t\tMichigan State Championship");
		System.out
				.println("mrcmp\t\tMid-Atlantic Robotics District Championship");
		System.out.println("necmp\t\tNew England District Championship");
		System.out.println("incmp\t\tIndiana State Championship");
		System.out.println("\nFIRST Championship Event");
		System.out.println("cmp\t\tEinstein Field");
		System.out.println("arc\t\tArchimedes Subdivision");
		System.out.println("cur\t\tCurie Subdivision");
		System.out.println("gal\t\tGalileo Subdivision");
		System.out.println("new\t\tNewton Subdivision");
		System.out.println("cars\t\tCarson Subdivision");
		System.out.println("carv\t\tCarver Subdivision");
		System.out.println("hop\t\tHopper Subdivision");
		System.out.println("tes\t\tTesla Subdivision");

	}


}
