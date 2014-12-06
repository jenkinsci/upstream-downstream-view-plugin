package hudson.plugins;

import hudson.Extension;
import hudson.Functions;
import hudson.model.Descriptor;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.views.ListViewColumn;

import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

/**
 * UpstreamDownstreamViewColumn, this plugin allows used to see the two additional
 * columns containing the Upstream and Downstream jobs for the particular job.
 * Additionally this plugin in the current version cut the column length to 50 chars
 *  in case if the job name summary length is less than the 50 or it put one job name
 *  in the column in case if the job name is fewer than 50 chars.
 * 
 * 12/05/2011
 * 
 * @author Kenji Kawaji
 */
public class UpDownStreamViewColumn extends ListViewColumn {

    public static final int UPSTREAM = 1;
    public static final int DOWNSTREAM = 2;
    public static final String NOT_AVAILABLE = "N/A";

    /**
     * This method will returns the HTML representation of the
     * Upstream/Downstream jobs for the particular master job.
     *
     * @return HTML String containing the Upstream/Downstream jobs under the
     *         Job (when available).
     */
    public String getStreamInfo(Job job, int streamType, String rootUrl) {
        if (!(job instanceof AbstractProject<?, ?>))
            return "";

        AbstractProject<?, ?> project = (AbstractProject<?, ?>) job;
        if (streamType == UPSTREAM) {
            return getHTMLProjectInfo(project.getUpstreamProjects(), rootUrl);
        }
        if (streamType == DOWNSTREAM) {
            return getHTMLProjectInfo(project.getDownstreamProjects(), rootUrl);
        }

        throw new IllegalArgumentException();
    }

    // TODO: there is no need to trim the job name to 50 since the name is breakable
    private String getHTMLProjectInfo(List <AbstractProject> lst, String rootUrl) {
        if (lst == null || lst.isEmpty()) return NOT_AVAILABLE;

        StringBuilder expression = new StringBuilder();

        for (AbstractProject prj: lst) {
            String linkString = String.format(
                    "<a class=\"model-link inside\" href=\"%s/%s\">%s</a>",
                    rootUrl, prj.getUrl(), Functions.breakableString(prj.getFullDisplayName())
            );

            expression.append(linkString).append(' ');
        }

        return expression.toString();
    }

    @Extension
    public static final Descriptor<ListViewColumn> DESCRIPTOR = new Descriptor<ListViewColumn>() {
        @Override
        public ListViewColumn newInstance(StaplerRequest req, JSONObject formData) {
            return new UpDownStreamViewColumn();
        }

        @Override
        public String getDisplayName() {
            return "Upstream Downstream Links";
        }
    };

    @Override
    public Descriptor<ListViewColumn> getDescriptor() {
        return DESCRIPTOR;
    }
}