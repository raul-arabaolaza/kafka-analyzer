# Next Steps

* Separate into two (or maybe three) distinct services
    * SourceProducer
    * TextAnalysis
    * Dashboard (TBD)
* Proper CQRS
    * Currently there is a mix of events and command without 
    proper organization, resulting in duplicated events
* Proper handling of errors and clean up of resources
* Contanerize services and kafka
    * Using Kubernetes
