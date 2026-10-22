# Night City Consumer Workflows

The [consumer benchmark][source] measures complete success, rejection with explicit rollback, recovery with a fresh
transaction, and repeated-read workflows against the installed library artifact. Configuration and caller-owned
relationship source setup are outside the timed region. Workload sizes and three independent samples are recorded in
the [measurement session][run]. No numeric gate or comparable baseline has been established.

The V1 closeout repeats the same workflow set against the locally installed `0.0.1` coordinate. Its source and consumer
POM identities differ from the earlier snapshot run, so the two sessions are retained as separate observations rather
than a direct before/after comparison. See the [V1 closeout session][closeout].

[source]: ../../../../examples/night_city/src/main/java/com/taitl/ex/examples/night_city/app/NightCityWorkflowBenchmark.java
[run]: ../runs/20261022T183337Z.NightCityConsumerWorkflows.md
[closeout]: ../runs/20261022T205600Z.NightCityV1Closeout.md
