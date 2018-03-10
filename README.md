# Next Steps

* Separate into two (or maybe three) distinct services
    * SourceProducer
    * TextAnalysis
    * Dashboard (TBD)
* Contanerize services and kafka
    * Using Kubernetes
* Convert every service to EventSourced
* Proper handling of errors and clean up of resources
* Use Sagas to implement the whole lifecycle?
    * From external request to data in dashboard
