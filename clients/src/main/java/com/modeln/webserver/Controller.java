package com.modeln.webserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modeln.enums.invoicelineitem.Status;
import com.modeln.enums.memberstateproposal.MemberStateProposalStatus;
import com.modeln.flows.bidaward.initiator.AddAndBroadcastBidAward;
import com.modeln.flows.invoicelineitem.initiator.AddInvoiceLineItemRequest;
import com.modeln.flows.invoicelineitem.initiator.RespondToInvoiceLineItemRequest;
import com.modeln.flows.memberState.initiators.AddMemberRequestProposal;
import com.modeln.flows.memberState.initiators.RespondToAddMemberRequestProposalRequest;
import com.modeln.flows.membershipState.initiator.AddMemberShipStateRequest;
import com.modeln.schema.bidawards.BidAwardStateSchema;
import com.modeln.schema.invoicelineitem.InvoiceLineItemStateSchema;
import com.modeln.schema.memberstate.MemberStateProposalSchema;
import com.modeln.states.bidawards.BidAwardState;
import com.modeln.states.invoicelineitem.InvoiceLineItemState;
import com.modeln.states.membershipstate.MemberShipState;
import com.modeln.states.memberstate.MemberStateProposal;
import net.corda.client.jackson.JacksonSupport;
import com.modeln.states.memberstate.MemberState;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.FieldInfo;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);
    private final CordaX500Name me;

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();

    }

    @Configuration
    class Plugin {
        @Bean
        public ObjectMapper registerModule() {
            return JacksonSupport.createNonRpcMapper();
        }
    }

    /** Helpers for filtering the network map cache. */
    public String toDisplayString(X500Name name){
        return BCStyle.INSTANCE.toString(name);
    }

    private boolean isNotary(NodeInfo nodeInfo) {
        return !proxy.notaryIdentities()
                .stream().filter(el -> nodeInfo.isLegalIdentity(el))
                .collect(Collectors.toList()).isEmpty();
    }

    private boolean isMe(NodeInfo nodeInfo){
        return nodeInfo.getLegalIdentities().get(0).getName().equals(me);
    }

    private boolean isNetworkMap(NodeInfo nodeInfo){
        return nodeInfo.getLegalIdentities().get(0).getName().getOrganisation().equals("Network Map Service");
    }

    @GetMapping(value = "/templateendpoint", produces = "text/plain")
    private String templateendpoint() {
        return "Define an endpoint here.";
    }

    @GetMapping(value = "/me",produces = APPLICATION_JSON_VALUE)
    private HashMap<String, String> whoami(){
        HashMap<String, String> myMap = new HashMap<>();
        myMap.put("me", me.toString());
        return myMap;
    }

    @GetMapping(value = "myName", produces = APPLICATION_JSON_VALUE)
    public HashMap<String, String> myName(){
        HashMap<String, String> myMap = new HashMap<>();
        myMap.put("myName", me.getOrganisation());
        return myMap;
    }

    @GetMapping(value = "name/{nameID}", produces = APPLICATION_JSON_VALUE)
    public HashMap<String, String> getName(@PathVariable("nameID") String nameID){
        HashMap<String, String> myMap = new HashMap<>();
        for(String string: nameID.split(",")){
            string = string.trim();
            if(string.startsWith("O=")){
                myMap.put("name", string.split("=")[1]);
                return myMap;
            }
        }
        return myMap;
    }


    @GetMapping(value = "/status", produces = TEXT_PLAIN_VALUE)
    private String status() {
        return "200";
    }

    @GetMapping(value = "/servertime", produces = TEXT_PLAIN_VALUE)
    private String serverTime() {
        return (LocalDateTime.ofInstant(proxy.currentNodeTime(), ZoneId.of("UTC"))).toString();
    }

    @GetMapping(value = "/addresses", produces = TEXT_PLAIN_VALUE)
    private String addresses() {
        return proxy.nodeInfo().getAddresses().toString();
    }

    @GetMapping(value = "/identities", produces = TEXT_PLAIN_VALUE)
    private String identities() {
        return proxy.nodeInfo().getLegalIdentities().toString();
    }

    @GetMapping(value = "/platformversion", produces = TEXT_PLAIN_VALUE)
    private String platformVersion() {
        return Integer.toString(proxy.nodeInfo().getPlatformVersion());
    }

    @GetMapping(value = "/peers", produces = APPLICATION_JSON_VALUE)
    public HashMap<String, List<String>> getPeers() {
        HashMap<String, List<String>> myMap = new HashMap<>();

        // Find all nodes that are not notaries, ourself, or the network map.
        Stream<NodeInfo> filteredNodes = proxy.networkMapSnapshot().stream()
                .filter(el -> !isNotary(el) && !isMe(el) && !isNetworkMap(el));
        // Get their names as strings
        List<String> nodeNames = filteredNodes.map(el -> el.getLegalIdentities().get(0).getName().toString())
                .collect(Collectors.toList());

        myMap.put("peers", nodeNames);
        return myMap;
    }

    @GetMapping(value = "/notaries", produces = TEXT_PLAIN_VALUE)
    private String notaries() {
        return proxy.notaryIdentities().toString();
    }

    @GetMapping(value = "/flows", produces = TEXT_PLAIN_VALUE)
    private String flows() {
        return proxy.registeredFlows().toString();
    }

    @GetMapping(value = "/states", produces = TEXT_PLAIN_VALUE)
    private String states() {
        return proxy.vaultQuery(ContractState.class).getStates().toString();
    }

    /*
    Member Proposal
     */

    @GetMapping(value = "/memberProposal",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<MemberStateProposal>> getMemberProposal() {
        return proxy.vaultQuery(MemberStateProposal.class).getStates();
    }

    @GetMapping(value = "/memberProposal/pending",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<MemberStateProposal>> getPendingMemberProposals() throws NoSuchFieldException{
        Field status = MemberStateProposalSchema.PersistMember.class.getDeclaredField("status");
        QueryCriteria queryCriteria =
                new QueryCriteria.VaultCustomQueryCriteria(
                        Builder.equal(status, 0)
                )
                        .withStatus(Vault.StateStatus.UNCONSUMED)
                        .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT)
                ;

        return proxy.vaultQueryByCriteria(queryCriteria, MemberStateProposal.class).getStates();
    }

    @GetMapping(value = "/memberProposal/{memberProposalId}/all",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<MemberStateProposal>> getAllMemberStateProposalForId(@PathVariable("memberProposalId") String memberProposalId) {
        QueryCriteria.LinearStateQueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria()
                .withUuid(Arrays.asList(UUID.fromString(memberProposalId)))
                .withStatus(Vault.StateStatus.ALL);
        return proxy.vaultQueryByCriteria(queryCriteria, MemberStateProposal.class).getStates();
    }

    @GetMapping(value = "/memberproposal/statuses",produces = APPLICATION_JSON_VALUE)
    public List<MemberStateProposalStatus> getMemberProposalStatuses() {
        return Arrays.asList(MemberStateProposalStatus.values());
    }

    @PostMapping(value = "/memberProposal", produces = APPLICATION_JSON_VALUE, headers =  "Content-Type=application/x-www-form-urlencoded")
    public ResponseEntity<String> createMemberProposal(HttpServletRequest request) {

        String memberName = request.getParameter("memberName");
        String memberType = request.getParameter("memberType");
        String description = request.getParameter("description");
        String DEAID = request.getParameter("DEAID");
        String DDDID = request.getParameter("DDDID");
        String memberStatus = request.getParameter("memberStatus");
        String address = request.getParameter("address");
        String memberStateProposalStatus = request.getParameter("memberStateProposalStatus");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String internalName = request.getParameter("internalName");
        String additionalInfo = request.getParameter("additionalInfo");


        try {
            UniqueIdentifier result = proxy.startTrackedFlowDynamic(AddMemberRequestProposal.class,
                    memberName,
                    memberType,
                    description,
                    DEAID,
                    DDDID,
                    memberStatus,
                    address,
                    MemberStateProposalStatus.valueOf(memberStateProposalStatus),
                            Instant.parse(startDate),
                            Instant.parse(endDate),
                            internalName,
                            additionalInfo)
                    .getReturnValue().get();
            // Return the response.
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Transaction id "+ result.getId() +" committed to ledger.");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }

    }

    @PostMapping(value = "/memberProposalResponse", produces = APPLICATION_JSON_VALUE, headers =  "Content-Type=application/x-www-form-urlencoded")
    public ResponseEntity<String> respondToCreateMemberProposal(HttpServletRequest request) {

        String memberStateProposalIdentifier = request.getParameter("memberStateProposalIdentifier");
        String memberStateProposalStatus = request.getParameter("memberStateProposalStatus");
        String memberName = request.getParameter("memberName");
        String memberType = request.getParameter("memberType");
        String description = request.getParameter("description");
        String DEAID = request.getParameter("DEAID");
        String DDDID = request.getParameter("DDDID");
        String memberStatus = request.getParameter("memberStatus");
        String address = request.getParameter("address");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String memberIdentifier = request.getParameter("memberIdentifier");


        try {
            UniqueIdentifier result = proxy.startTrackedFlowDynamic(RespondToAddMemberRequestProposalRequest.class,
                    new UniqueIdentifier(null, UUID.fromString(memberStateProposalIdentifier)),
                    memberName,
                    memberType,
                    description,
                    DEAID,
                    DDDID,
                    memberStatus,
                    address,
                    MemberStateProposalStatus.valueOf(memberStateProposalStatus),
                    Instant.parse(startDate),
                    Instant.parse(endDate)
                            )
                    .getReturnValue().get();
            // Return the response.
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Transaction id "+ result.getId() +" committed to ledger.");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }

    }


    /*
    Members
     */

    @GetMapping(value = "/members",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<MemberState>> getMembers() {
        return proxy.vaultQuery(MemberState.class).getStates();
    }

    @GetMapping(value = "/members/{memberId}",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<MemberState>> getMember(@PathVariable("memberId") String memberId) {
        QueryCriteria.LinearStateQueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria()
                .withUuid(Arrays.asList(UUID.fromString(memberId)));
        return proxy.vaultQueryByCriteria(queryCriteria, MemberState.class).getStates();
    }

    @GetMapping(value = "/membership",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<MemberShipState>> getMemberShips() {
        return proxy.vaultQuery(MemberShipState.class).getStates();
    }

    @GetMapping(value = "/membership/{memberShipId}",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<MemberShipState>> getMemberShip(@PathVariable("memberShipId") String memberShipId) {
        QueryCriteria.LinearStateQueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria()
                .withUuid(Arrays.asList(UUID.fromString(memberShipId)));
        return proxy.vaultQueryByCriteria(queryCriteria, MemberShipState.class).getStates();
    }

    @GetMapping(value = "/invoiceLineItem",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<InvoiceLineItemState>> getInvoiceLineItem() {
        return proxy.vaultQuery(InvoiceLineItemState.class).getStates();
    }

    @GetMapping(value = "/bidAward",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<BidAwardState>> getBidAwards() {
        return proxy.vaultQuery(BidAwardState.class).getStates();
    }

    @GetMapping(value = "/bidAward/{bidAwardId}",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<BidAwardState>> getBidAward(@PathVariable("bidAwardId") String bidAwardId) {
        QueryCriteria.LinearStateQueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria()
                .withUuid(Arrays.asList(UUID.fromString(bidAwardId)));
        return proxy.vaultQueryByCriteria(queryCriteria, BidAwardState.class).getStates();
    }

    @PostMapping(value = "/bidAward/query",produces = APPLICATION_JSON_VALUE, headers =  "Content-Type=application/x-www-form-urlencoded")
    public BidAwardState getBidAwardForQuery(HttpServletRequest request) throws NoSuchFieldException{
        String member = request.getParameter("member");
        String productNDC = request.getParameter("productNDC");
        String invoiceDate = request.getParameter("invoiceDate");
        Field memberField = BidAwardStateSchema.PersistMember.class.getDeclaredField("memberStateLinearPointer");
        Field productNDCField = BidAwardStateSchema.PersistMember.class.getDeclaredField("productNDC");
        /*Field startDateField = BidAwardStateSchema.PersistMember.class.getDeclaredField("startDate");
        Field endDateField = BidAwardStateSchema.PersistMember.class.getDeclaredField("endDate");*/

        CriteriaExpression productNDCIndex = Builder.equal(productNDCField, productNDC);
        CriteriaExpression memberIndex = Builder.equal(memberField, UUID.fromString(member));

        QueryCriteria productNDCCriteria = new QueryCriteria.VaultCustomQueryCriteria(productNDCIndex);
        QueryCriteria memberCriteria = new QueryCriteria.VaultCustomQueryCriteria(memberIndex);

        QueryCriteria criteria = productNDCCriteria.and(memberCriteria);

        Instant invoiceDateInstant = Instant.parse(invoiceDate);

        List<StateAndRef<BidAwardState>> bidAwardStatePage = proxy.vaultQueryByCriteria(criteria, BidAwardState.class).getStates();
        if(bidAwardStatePage != null && bidAwardStatePage.size() > 0) {
            for (StateAndRef<BidAwardState> bidAwardStateStateAndRef : bidAwardStatePage) {
                BidAwardState bidAwardState = bidAwardStateStateAndRef.getState().getData();
                if(invoiceDateInstant.isAfter(bidAwardState.getStartDate()) &&
                        invoiceDateInstant.isBefore(bidAwardState.getEndDate()))
                    return bidAwardState;
            }
        }
        return  null;
    }

    @GetMapping(value = "/invoiceLineItem/{lineItemId}",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<InvoiceLineItemState>> getInvoiceLineItemForId(@PathVariable("lineItemId") String lineItemId) {
        QueryCriteria.LinearStateQueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria()
                .withUuid(Arrays.asList(UUID.fromString(lineItemId)));
        return proxy.vaultQueryByCriteria(queryCriteria, InvoiceLineItemState.class).getStates();
    }

    @GetMapping(value = "/invoiceLineItem/{lineItemId}/all",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<InvoiceLineItemState>> getAllInvoiceLineItemForId(@PathVariable("lineItemId") String lineItemId) {
        QueryCriteria.LinearStateQueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria()
                .withUuid(Arrays.asList(UUID.fromString(lineItemId)))
                .withStatus(Vault.StateStatus.ALL);
        return proxy.vaultQueryByCriteria(queryCriteria, InvoiceLineItemState.class).getStates();
    }

    @GetMapping(value = "/invoiceLineItem/pending",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<InvoiceLineItemState>> getAllInvoiceLineItemForId() throws NoSuchFieldException{
        Field status = InvoiceLineItemStateSchema.PersistMember.class.getDeclaredField("status");
        QueryCriteria queryCriteria =
                new QueryCriteria.VaultCustomQueryCriteria(
                    Builder.equal(status, 0)
                )
                .withStatus(Vault.StateStatus.UNCONSUMED)
                .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT)
                ;

        return proxy.vaultQueryByCriteria(queryCriteria, InvoiceLineItemState.class).getStates();
    }



    @GetMapping(value = "/invoiceLineItem/statuses",produces = APPLICATION_JSON_VALUE)
    public List<Status> getInvoiceLineItemStatuses() {
        return Arrays.asList(Status.values());
    }



    @PostMapping(value = "/membership", produces = APPLICATION_JSON_VALUE, headers =  "Content-Type=application/x-www-form-urlencoded")
    public ResponseEntity<String> createMemberShip(HttpServletRequest request) {

        String memberStateUUID = request.getParameter("memberStateUUID");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        Instant startInstant = Instant.parse(startDate);
        Instant endInstant = Instant.parse(endDate);

        try {
            UniqueIdentifier result = proxy
                    .startTrackedFlowDynamic(AddMemberShipStateRequest.class, memberStateUUID,startInstant, endInstant)
                    .getReturnValue().get();
            // Return the response.
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Membership committed to ledger with id: "+ result.getId());
            // For the purposes of this demo app, we do not differentiate by exception type.
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping(value = "/bidAward", produces = APPLICATION_JSON_VALUE, headers =  "Content-Type=application/x-www-form-urlencoded")
    public ResponseEntity<String> createBidAward(HttpServletRequest request) {

        String bidAwardId = request.getParameter("bidAwardId");
        String memberStateUUID = request.getParameter("memberStateUUID");
        String productNDC = request.getParameter("productNDC");
        String wholesalerId = request.getParameter("wholesalerId");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        float wacPrice = Float.parseFloat(request.getParameter("wacPrice"));
        float authorizedPrice = Float.parseFloat(request.getParameter("authorizedPrice"));
        String broadcastToMembers = request.getParameter("broadcastToMembers");


        Instant startInstant = Instant.parse(startDate);
        Instant endInstant = Instant.parse(endDate);
        List<AbstractParty> broadcastList = new ArrayList<>();
        for(String party: broadcastToMembers.split(";")){
            CordaX500Name partyX500Name = CordaX500Name.parse(party);
            Party otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name);
            broadcastList.add(otherParty);
        }

        try {
            proxy
                    .startTrackedFlowDynamic(AddAndBroadcastBidAward.class,
                            bidAwardId,
                            UUID.fromString(memberStateUUID),
                            productNDC,
                            wholesalerId,
                            startInstant,
                            wacPrice,
                            authorizedPrice,
                            endInstant,
                            wholesalerId,
                            broadcastList
                            )
                    .getReturnValue().get();
            // Return the response.
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Transaction  committed to ledger");
            // For the purposes of this demo app, we do not differentiate by exception type.
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }

    }

    @PostMapping(value = "/invoiceLineItem", produces = APPLICATION_JSON_VALUE, headers =  "Content-Type=application/x-www-form-urlencoded")
    public ResponseEntity<String> createInvoiceLineItem(HttpServletRequest request) {

        String memberStateUniqueIdentifier = request.getParameter("memberStateUniqueIdentifier");
        String productNDC = request.getParameter("productNDC");
        String invoiceId = request.getParameter("invoiceId");
        String invoiceDate = request.getParameter("invoiceDate");
        String bidAwardUniqueIdentifier = request.getParameter("bidAwardUniqueIdentifier");
        String consumer = request.getParameter("consumer");
        String status = request.getParameter("status");
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        Instant invoiceInstant = Instant.parse(invoiceDate);
        CordaX500Name partyX500Name = CordaX500Name.parse(consumer);
        Party otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name);


        try {
            UniqueIdentifier result = proxy
                    .startTrackedFlowDynamic(AddInvoiceLineItemRequest.class,
                            new UniqueIdentifier(null, UUID.fromString(memberStateUniqueIdentifier)),
                            productNDC,
                            invoiceId,
                            invoiceInstant,
                            new UniqueIdentifier(null, UUID.fromString(bidAwardUniqueIdentifier)),
                            otherParty,
                            Status.valueOf(status),
                            quantity
                    )
                    .getReturnValue().get();
            // Return the response.
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Transaction  committed to ledger with identifier as: " + result.getId());
            // For the purposes of this demo app, we do not differentiate by exception type.
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }

    }

    @PostMapping(value = "/invoiceLineItem/{linearId}", produces = APPLICATION_JSON_VALUE, headers =  "Content-Type=application/x-www-form-urlencoded")
    public ResponseEntity<String> createInvoiceLineItem(HttpServletRequest request, @PathVariable("linearId") String linearId) {

        String status = request.getParameter("status");



        try {
            UniqueIdentifier result = proxy
                    .startTrackedFlowDynamic(RespondToInvoiceLineItemRequest.class,
                            new UniqueIdentifier(null, UUID.fromString(linearId)),
                            Status.valueOf(status)
                    )
                    .getReturnValue().get();
            // Return the response.
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Transaction  committed to ledger with identifier as: " + result.getId());
            // For the purposes of this demo app, we do not differentiate by exception type.
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }

    }
}