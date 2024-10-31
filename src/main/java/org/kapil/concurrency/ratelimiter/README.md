Rate Limiter
============

### Fixed Window Rate Limiter

In the Fixed Window Algorithm, time is divided into fixed intervals or windows, such as seconds,
minutes, or hours. Each window has a maximum number of requests that can be processed. Once the
limit is reached within a window, any further requests are blocked or throttled until the next
window starts.

Example: If the rate limit is set to 3 requests per minute, and the time window starts at 12:00:00,
then between 12:00:00 and 12:00:59, only 3 requests will be accepted. At 12:01:00, the counter
resets, and another 3 requests can be accepted.

* Suitable for applications where precise rate limiting is not critical and occasional bursts are
  acceptable.
* Good for APIs with consistent traffic and low tolerance for complexity.

### Sliding Window Rate Limiter

The Sliding Window Algorithm, also known as the Rolling Window Algorithm, addresses the drawbacks of
the Fixed Window Algorithm by allowing a continuous, moving window of time to limit requests.
Instead of resetting at fixed intervals, the sliding window checks the rate limit based on the last
request timestamp and a rolling count of the requests within the window duration.

Example: If the rate limit is set to 3 requests per minute, the algorithm maintains a rolling count
of requests within the last 60 seconds at any given time. For each new request, it checks if the
number of requests in the past 60 seconds exceeds the limit. If yes, the request is blocked; if no,
the request is processed and the count is updated.

* Ideal for scenarios where a smoother rate limiting approach is required, with fair handling
  throughout the window.
* Useful when you need a more precise control over the rate limiting and can afford the additional
  memory overhead.

### Token Bucket Rate Limiter

Another popular algorithm is the Token Bucket Algorithm, which allows for a certain burstiness in
request rates while enforcing a rate limit over a longer period. Tokens are added to the bucket at a
constant rate, and each request consumes a token. If there are no tokens left, the request is
blocked or throttled.

The token bucket algorithm takes two parameters:

* Bucket size: the maximum number of tokens allowed in the bucket.
* Refill rate: the number of tokens put into the bucket every second

Example: If the rate limit is set to 3 requests per minute, tokens are added to the bucket at a rate
of 1 token every 20 seconds. The bucket can hold a maximum of 3 tokens. When a request comes in, it
consumes a token. If the bucket is empty, the request is blocked.

* Suitable for applications where short bursts of high traffic are common but need to be averaged
  out over time.
* Effective for APIs or services that must handle varied request patterns or need to allow for
  short-term exceeding of rate limits w

### Leaky Bucket Rate Limiter

The Leaky Bucket Algorithm is similar to the Token Bucket Algorithm but focuses on smoothing out the
traffic. Requests are added to a queue (bucket) and processed at a fixed rate. If the queue is full,
incoming requests are dropped.

It is usually implemented with a first-in-first-out (FIFO) queue. The algorithm works as follows:

* When a request arrives, the system check if the queue is full. If it is not full, the request is
added to the queue, otherwise, the request is dropped.
* The requests in the queue are processed at a fixed rate.

The leaking bucket algorithm takes the following two parameters:
* Bucket size: the maximum number of requests allowed in the queue.
* Outflow rate: the number of requests that can be processed at a fixed rate by the services.

* Beneficial for applications that need to process requests at a consistent pace, such as network
  traffic shaping or controlling write operations to disk.
* Useful where it’s crucial to avoid burstiness impacting downstream systems.

ithout compromising long-term boundaries.