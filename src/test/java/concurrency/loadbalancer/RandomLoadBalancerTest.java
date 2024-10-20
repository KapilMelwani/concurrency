package concurrency.loadbalancer;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.kapil.concurrency.loadbalancer.Instance;
import org.kapil.concurrency.loadbalancer.LoadBalancer;
import org.kapil.concurrency.loadbalancer.RandomLoadBalancer;

public class RandomLoadBalancerTest {
  @Test
  public void testNextInstanceReturnsInstance() throws InterruptedException {
    Instance instance1 = new Instance("192.168.0.1");
    LoadBalancer loadBalancer = new RandomLoadBalancer(List.of(instance1));

    Optional<Instance> instance = loadBalancer.nextInstance();

    assertTrue(instance.isPresent(), "Should return an instance");
    assertEquals("192.168.0.1", instance.get().getIp(), "Should return the correct instance");
  }
}
