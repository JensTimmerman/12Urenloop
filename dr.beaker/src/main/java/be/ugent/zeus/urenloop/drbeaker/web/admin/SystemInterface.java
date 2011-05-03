package be.ugent.zeus.urenloop.drbeaker.web.admin;

import be.ugent.zeus.urenloop.drbeaker.ScoreManager;
import be.ugent.zeus.urenloop.drbeaker.StickManager;
import be.ugent.zeus.urenloop.drbeaker.TeamManager;
import be.ugent.zeus.urenloop.drbeaker.db.Stick;
import be.ugent.zeus.urenloop.drbeaker.db.Team;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.view.Viewable;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 *
 * @author Thomas Meire
 */
@Path("/admin/system")
public class SystemInterface {

  private static final Logger logger = Logger.getLogger("12UL");
  
  private ScoreManager scoreManager = ScoreManager.lookup();

  private StickManager stickManager = StickManager.lookup();

  private TeamManager teamManager = TeamManager.lookup();

  @GET
  public Viewable showCounterSwitch() {
    return new Viewable("/admin/system.jsp", scoreManager.getSource());
  }

  @POST
  public Response showCounterSwitch(@FormParam("name") String name) {
    scoreManager.setCurrentSource(name);
    return Response.seeOther(URI.create("/admin/system")).build();
  }

  @POST
  @Path("bootstrap")
  public Response bootstrap(@FormParam("data") String data) {
    data.split("\n");
    for (String line : data.split("\n")) {
      String[] x = line.trim().split(" ", 3);

      long id = Long.parseLong(x[0]);

      Stick stick = new Stick();
      stick.setId(id);
      stick.setMac(x[1]);

      stickManager.add(stick);

      if (x.length == 3) {
        Team team = new Team();
        team.setName(x[2]);
        teamManager.add(team);

        teamManager.assign(team, stickManager.get(id));
      }
    }
    return Response.seeOther(URI.create("/admin")).build();
  }

  @POST
  @Path("reset")
  public Response reset(@Context HttpServletRequest request) {
    logger.log(Level.WARNING, "USER {0} RESETS THE WHOLE SYSTEM!!", new Object[]{request.getUserPrincipal().getName()});
    
    scoreManager.reset();
    
    return Response.seeOther(URI.create("/admin/history")).build();
  }
}