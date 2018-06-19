package IBLink;

import com.ib.client.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adapter pattern: provides empty implementation for all the methods in the
 * interface, so that the implementing classes can selectively override only
 * the needed methods.
 */
public class EWrapperAdapter implements EWrapper {

  public void tickPrice(int i, int i1, double d, TickAttr ta) {}

  public void tickSize(int i, int i1, int i2) {}

  public void tickOptionComputation(int i, int i1, double d, double d1, double d2, double d3, double d4, double d5, double d6, double d7) {}

  public void tickGeneric(int i, int i1, double d) {}

  public void tickString(int i, int i1, String string) {}

  public void tickEFP(int i, int i1, double d, String string, double d1, int i2, String string1, double d2, double d3) {}

  public void orderStatus(int i, String string, double d, double d1, double d2, int i1, int i2, double d3, int i3, String string1, double d4) {}

  public void openOrder(int i, Contract cntrct, Order order, OrderState os) {}

  public void openOrderEnd() {}

  public void updateAccountValue(String string, String string1, String string2, String string3) {}

  public void updatePortfolio(Contract cntrct, double d, double d1, double d2, double d3, double d4, double d5, String string) {}

  public void updateAccountTime(String string) {}

  public void accountDownloadEnd(String string) {}

  public void nextValidId(int i) {}

  public void contractDetails(int i, ContractDetails cd) {}

  public void bondContractDetails(int i, ContractDetails cd) {}

  public void contractDetailsEnd(int i) {}

  public void execDetails(int i, Contract cntrct, Execution exctn) {}

  public void execDetailsEnd(int i) {}

  public void updateMktDepth(int i, int i1, int i2, int i3, double d, int i4) {}

  public void updateMktDepthL2(int i, int i1, String string, int i2, int i3, double d, int i4) {}

  public void updateNewsBulletin(int i, int i1, String string, String string1) {}

  public void managedAccounts(String string) {}

  public void receiveFA(int i, String string) {}

  public void historicalData(int i, Bar bar) {}
  
  public void scannerParameters(String string) {}

  public void scannerData(int i, int i1, ContractDetails cd, String string, String string1, String string2, String string3) {}

  public void scannerDataEnd(int i) {}

  public void realtimeBar(int i, long l, double d, double d1, double d2, double d3, long l1, double d4, int i1) {}

  public void currentTime(long l) {}

  public void fundamentalData(int i, String string) {}

  public void deltaNeutralValidation(int i, DeltaNeutralContract dnc) {}

  public void tickSnapshotEnd(int i) {}

  public void marketDataType(int i, int i1) {}

  public void commissionReport(CommissionReport cr) {}

  public void position(String string, Contract cntrct, double d, double d1) {}

  public void positionEnd() {}

  public void accountSummary(int i, String string, String string1, String string2, String string3) {}

  public void accountSummaryEnd(int i) {}

  public void verifyMessageAPI(String string) {}

  public void verifyCompleted(boolean bln, String string) {}

  public void verifyAndAuthMessageAPI(String string, String string1) {}

  public void verifyAndAuthCompleted(boolean bln, String string) {}

  public void displayGroupList(int i, String string) {}

  public void displayGroupUpdated(int i, String string) {}

  public void error(Exception excptn) {}

  public void error(String string) {}

  public void error(int i, int i1, String string) {}

  public void connectionClosed() {}

  public void connectAck() {}

  public void positionMulti(int i, String string, String string1, Contract cntrct, double d, double d1) {}

  public void positionMultiEnd(int i) {}

  public void accountUpdateMulti(int i, String string, String string1, String string2, String string3, String string4) {}

  public void accountUpdateMultiEnd(int i) {}

  public void securityDefinitionOptionalParameter(int i, String string, int i1, String string1, String string2, Set<String> set, Set<Double> set1) {}

  public void securityDefinitionOptionalParameterEnd(int i) {}

  public void softDollarTiers(int i, SoftDollarTier[] sdts) {}

  public void familyCodes(FamilyCode[] fcs) {}

  public void symbolSamples(int i, ContractDescription[] cds) {}

  public void historicalDataEnd(int i, String string, String string1) {}

  public void mktDepthExchanges(DepthMktDataDescription[] dmdds) {}

  public void tickNews(int i, long l, String string, String string1, String string2, String string3) {}

  public void smartComponents(int i, Map<Integer, Map.Entry<String, Character>> map) {}

  public void tickReqParams(int i, double d, String string, int i1) {}

  public void newsProviders(NewsProvider[] nps) {}

  public void newsArticle(int i, int i1, String string) {}

  public void historicalNews(int i, String string, String string1, String string2, String string3) {}

  public void historicalNewsEnd(int i, boolean bln) {}

  public void headTimestamp(int i, String string) {}

  public void histogramData(int i, List<HistogramEntry> list) {}

  public void historicalDataUpdate(int i, Bar bar) {}

  public void rerouteMktDataReq(int i, int i1, String string) {}

  public void rerouteMktDepthReq(int i, int i1, String string) {}

  public void marketRule(int i, PriceIncrement[] pis) {}

  public void pnl(int i, double d, double d1, double d2) {}

  public void pnlSingle(int i, int i1, double d, double d1, double d2, double d3) {}

  public void historicalTicks(int i, List<HistoricalTick> list, boolean bln) {}

  public void historicalTicksBidAsk(int i, List<HistoricalTickBidAsk> list, boolean bln) {}

  public void historicalTicksLast(int i, List<HistoricalTickLast> list, boolean bln) {}

  public void tickByTickAllLast(int i, int i1, long l, double d, int i2, TickAttr ta, String string, String string1) {}

  public void tickByTickBidAsk(int i, long l, double d, double d1, int i1, int i2, TickAttr ta) {}

  public void tickByTickMidPoint(int i, long l, double d) {}
  
}
