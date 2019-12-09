package ribbonconfiguration;

import com.javaedge.contentcenter.configuration.NacosSameClusterWeightedRule;
import com.javaedge.contentcenter.configuration.NacosWeightedRule;
import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author JavaEdge
 */
@Configuration
public class RibbonConfiguration {

    @Bean
    public IRule ribbonRule() {
        return new NacosSameClusterWeightedRule();
    }
}
