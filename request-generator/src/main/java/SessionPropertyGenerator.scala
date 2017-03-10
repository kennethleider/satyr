import com.canoeventures.common_cao.domain.Session
import com.canoeventures.common_cao.util.SerializationUtils


class SessionPropertyGenerator(operatorSelector : Selector, providerIdSelector : Selector, platformSelector : Selector, regionSelectors : Map[String, Selector], backOfficeSelector : Map[String, Selector]) extends Iterator[Array[Byte]] {
   val serializer = new SerializationUtils[Session](Session.SCHEMA$).serialize _

   override def hasNext: Boolean = true

   override def next(): Array[Byte] = {
      val operator = operatorSelector.next()
      val session = new Session
      session.setOperator(operator)
      session.setProviderId(providerIdSelector.next())
      session.setPlatform(platformSelector.next())
      session.setRegion(regionSelectors(operator).next())
      session.setVodBackOffice(backOfficeSelector(operator).next())
      session.setTransactionId("transactionId")
      session.setWrapperDateTime("date-time")
      session.setWrapperAdm("192.0.0.1")
      session.setWrapperAds("192.0.0.1")
      session.setWrapperNw("1921243")
      session.setSessionId("sessionId")
      session.setSessionStartDateTime("date-time")
      session.setAssetId("assetId")
      session.setAcceptedDateTime("date-time")
      session.setProvider("unspecified")
      session.setNetwork("unspecified")
      session.setProgrammer("unspecified")
      session.setDeviceId("unspecified")
      session.setHouseholdId("unspecified")

      serializer(session)
   }
}
