package hbm.fraudDetectionSystem.DemografiEngine.Customer;

import hbm.fraudDetectionSystem.DemografiEngine.Account.Account;
import hbm.fraudDetectionSystem.DemografiEngine.Account.AccountRepository;
import hbm.fraudDetectionSystem.DemografiEngine.Address.Address;
import hbm.fraudDetectionSystem.DemografiEngine.Address.AddressRepository;
import hbm.fraudDetectionSystem.DemografiEngine.Card.Card;
import hbm.fraudDetectionSystem.DemografiEngine.Card.CardRepository;
import hbm.fraudDetectionSystem.DemografiEngine.Person.Person;
import hbm.fraudDetectionSystem.DemografiEngine.Person.PersonDetails;
import hbm.fraudDetectionSystem.DemografiEngine.Person.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class CustomerServiceImpl implements CustomerService {
    private final String InputFolder = "DemografiData/Input";
    private final String OutputFolder = "DemografiData/Output";
    private final String ErrorFolder = "DemografiData/Error";
    private Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private CustomerRepository repository;
    private CardRepository cardRepository;
    private AccountRepository accountRepository;
    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository repository, CardRepository cardRepository, AccountRepository accountRepository, PersonRepository personRepository, AddressRepository addressRepository) {
        this.repository = repository;
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.personRepository = personRepository;
        this.addressRepository = addressRepository;
    }

    @Scheduled(fixedDelayString = "${demografi.delay.interval}")
    public void processXmlFiles() {
        File inputFolder = new File(InputFolder);
        File outputFolder = new File(OutputFolder);
        File errorFolder = new File(ErrorFolder);
        if (!inputFolder.isDirectory()) {
            LOGGER.warn("Input is not a directory!");
            return;
        }
        File[] files = inputFolder.listFiles();
        for (File file : files) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String today = LocalDate.now().format(dateFormatter);
            String fileNamePattern = "1_customer_" + today + "_\\d+\\.xml";
            if (file.isFile() && file.getName().matches(fileNamePattern)) {
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(file);
                    if (document.getDocumentElement().getChildNodes().getLength() == 0) {
                        LOGGER.warn("File XML is empty: " + file.getName());
                        moveFile(file, errorFolder);
                        continue;
                    }
                    extractDataFromXml(document);
                    moveFile(file, outputFolder);
                    LOGGER.info("File successfully processed: " + file.getName());
                } catch (Exception e) {
                    LOGGER.error("Error processing XML file: " + file.getName());
                    LOGGER.error("Detail Error: " + e.getMessage());
                    moveFile(file, errorFolder);
                }
            } else {
                LOGGER.warn("Invalid file: " + file.getName());
                moveFile(file, errorFolder);
            }
        }
    }

    private void moveFile(File sourceFile, File destinationFolder) {
        File destinationFile = new File(destinationFolder.getAbsolutePath(), sourceFile.getName());
        sourceFile.renameTo(destinationFile);
    }

    private void extractDataFromXml(Document document) throws Exception {
        List<Customer> listCustomer = new ArrayList<>();
        NodeList custList = document.getElementsByTagName("customer");
        for (int cust = 0; cust < custList.getLength(); cust++) {
            Node custNode = custList.item(cust);
            if (custNode.getNodeType() == Node.ELEMENT_NODE) {
                Element custElement = (Element) custNode;
                String custNumber = getElementValue(custElement, "custNumber");
                String custCategory = getElementValue(custElement, "custCategory");
                String custRelation = getElementValue(custElement, "custRelation");
                int resident = Integer.parseInt(getElementValue(custElement, "resident"));
                int nationality = Integer.parseInt(getElementValue(custElement, "nationality"));
                String creditRating = getElementValue(custElement, "creditRating");
                String moneyLaundryRisk = getElementValue(custElement, "moneyLaundryRisk");
                String moneyLaundryReason = getElementValue(custElement, "moneyLaundryReason");
                String entityType = getElementValue(custElement, "entityType");

                List<Customer> existingDataCustInArray = listCustomer.stream().filter(existData -> Objects.equals(existData.getCustNumber(), custNumber)).collect(Collectors.toList());
                if (existingDataCustInArray.size() > 0) {
                    throw new Exception(String.format("Customer Number %s duplicate detected", custNumber));
                }

                Customer customerData = repository.findByCustNumber(custNumber);
                if (customerData != null) {
                    customerData.setCustCategory(custCategory);
                    customerData.setCustRelation(custRelation);
                    customerData.setResident(resident);
                    customerData.setNationality(nationality);
                    customerData.setCreditRating(creditRating);
                    customerData.setMoneyLaundryRisk(moneyLaundryRisk);
                    customerData.setMoneyLaundryReason(moneyLaundryReason);
                    customerData.setEntityType(entityType);
                    customerData = repository.save(customerData);
                } else {
                    customerData = new Customer();
                    customerData.setCustNumber(custNumber);
                    customerData.setCustCategory(custCategory);
                    customerData.setCustRelation(custRelation);
                    customerData.setResident(resident);
                    customerData.setNationality(nationality);
                    customerData.setCreditRating(creditRating);
                    customerData.setMoneyLaundryRisk(moneyLaundryRisk);
                    customerData.setMoneyLaundryReason(moneyLaundryReason);
                    customerData.setEntityType(entityType);

                    //
                    customerData = repository.save(customerData);
                }

                listCustomer.add(customerData);

                Node personNode = custElement.getElementsByTagName("person").item(0);
                Node personDetails = custElement.getElementsByTagName("personDetails").item(0);
                if (personNode != null && personNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element personElement = (Element) personNode;

                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                    String personTitle = getElementValue(personElement, "personTitle");
                    String personName = getElementValue(personElement, "personName");
                    String suffix = getElementValue(personElement, "suffix");
                    String dateBirthday = getElementValue(personElement, "birthday");
                    String placeOfBirth = getElementValue(personElement, "placeOfBirth");
                    String gender = getElementValue(personElement, "gender");

                    Date parsedDate = inputFormat.parse(dateBirthday);
                    Timestamp birthday = new Timestamp(parsedDate.getTime());

                    Person personData = personRepository.findByPersonName(personName);

                    if (personData != null) {
                        personData.setPersonTitle(personTitle);
                        personData.setPersonName(personName);
                        personData.setSuffix(suffix);
                        personData.setBirthday(birthday);
                        personData.setPlaceOfBirth(placeOfBirth);
                        personData.setGender(gender);
                        personData = personRepository.save(personData);
                    } else {
                        personData = new Person();
                        personData.setPersonTitle(personTitle);
                        personData.setPersonName(personName);
                        personData.setSuffix(suffix);
                        personData.setBirthday(birthday);
                        personData.setPlaceOfBirth(placeOfBirth);
                        personData.setGender(gender);
                        if (personDetails != null && personDetails.getNodeType() == Node.ELEMENT_NODE) {
                            Element personDetailsElement = (Element) personDetails;
                            String surname = getElementValue(personDetailsElement, "surname");
                            String firstName = getElementValue(personDetailsElement, "firstName");
                            String secondName = getElementValue(personDetailsElement, "secondName");

                            PersonDetails details = new PersonDetails();
                            details.setSurname(surname);
                            details.setFirstName(firstName);
                            details.setSecondName(secondName);
                            personData.setPersonDetails(details);
                        }
                        personData.setCustomer(customerData);
                        personData = personRepository.save(personData);

                    }
                    Node addressNode = custElement.getElementsByTagName("address").item(0);
                    if (addressNode != null && addressNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element addressElement = (Element) addressNode;
                        String addressType = getElementValue(addressElement, "addressType");
                        String country = getElementValue(addressElement, "country");
                        String addressName = getElementValue(addressElement, "addressName");
                        String region = getElementValue(addressElement, "region");
                        String city = getElementValue(addressElement, "city");
                        String street = getElementValue(addressElement, "street");
                        String house = getElementValue(addressElement, "house");
                        String apartment = getElementValue(addressElement, "apartment");
                        String postalCode = getElementValue(addressElement, "postalCode");
                        String placeCode = getElementValue(addressElement, "placeCode");
                        String regionCode = getElementValue(addressElement, "regionCode");
                        float latitude = Float.parseFloat(getElementValue(addressElement, "latitude"));
                        float longitude = Float.parseFloat(getElementValue(addressElement, "longitude"));

                        Address address = new Address();
                        address.setAddressType(addressType);
                        address.setCountry(country);
                        address.setAddressName(addressName);
                        address.setRegion(region);
                        address.setCity(city);
                        address.setStreet(street);
                        address.setHouse(house);
                        address.setApartment(apartment);
                        address.setPostalCode(postalCode);
                        address.setPlaceCode(placeCode);
                        address.setRegionCode(regionCode);
                        address.setLatitude(latitude);
                        address.setLongitude(longitude);

                        address.setPerson(personData);
                        addressRepository.save(address);
                    }
                }
                List<Card> listCard = new ArrayList<>();
                NodeList cardList = custElement.getElementsByTagName("cardList");
                for (int i = 0; i < cardList.getLength(); i++) {
                    Node cardNode = cardList.item(i);
                    if (cardNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element cardElement = (Element) cardNode;
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                        String cardNumber = getElementValue(cardElement, "cardNumber");
                        String cardMask = getElementValue(cardElement, "cardMask");
                        String cardId = getElementValue(cardElement, "cardId");
                        String cardIssDateStr = getElementValue(cardElement, "cardIssDate");
                        String cardStartDateStr = getElementValue(cardElement, "cardStartDate");
                        String expirationDateStr = getElementValue(cardElement, "expirationDate");
                        int instanceId = Integer.parseInt(getElementValue(cardElement, "instanceId"));
                        int precedingInstanceId = Integer.parseInt(getElementValue(cardElement, "precedingInstanceId"));
                        int sequentialNumber = Integer.parseInt(getElementValue(cardElement, "sequentialNumber"));
                        String cardStatus = getElementValue(cardElement, "cardStatus");
                        String cardState = getElementValue(cardElement, "cardState");
                        String category = getElementValue(cardElement, "category");
                        int pvv = Integer.parseInt(getElementValue(cardElement, "pvv"));
                        int pinOffset = Integer.parseInt(getElementValue(cardElement, "pinOffset"));
                        boolean pinUpdateFlag = Boolean.parseBoolean(getElementValue(cardElement, "pinUpdateFlag"));
                        int cardTypeId = Integer.parseInt(getElementValue(cardElement, "cardTypeId"));
                        String prevCardNumber = getElementValue(cardElement, "prevCardNumber");
                        String prevCardId = getElementValue(cardElement, "prevCardId");
                        String agentNumber = getElementValue(cardElement, "agentNumber");
                        String agentName = getElementValue(cardElement, "agentName");
                        String productNumber = getElementValue(cardElement, "productNumber");
                        String productName = getElementValue(cardElement, "productName");
                        String companyName = getElementValue(cardElement, "companyName");
                        String serviceCode = getElementValue(cardElement, "serviceCode");

                        Date parsedDate1 = inputFormat.parse(cardIssDateStr);
                        Date parsedDate2 = inputFormat.parse(cardStartDateStr);
                        Date parsedDate3 = inputFormat.parse(expirationDateStr);

                        Timestamp cardIssDate = new Timestamp(parsedDate1.getTime());
                        Timestamp cardStartDate = new Timestamp(parsedDate2.getTime());
                        Timestamp expirationDate = new Timestamp(parsedDate3.getTime());


                        List<Card> existingDataCardInArray = listCard.stream().filter(existData -> Objects.equals(existData.getCardNumber(), cardNumber)).collect(Collectors.toList());
                        if (existingDataCardInArray.size() > 0) {
                            throw new Exception(String.format("Card Number %s duplicate detected", cardNumber));
                        }

                        Card cardData = cardRepository.findByCardNumber(cardNumber);
                        if (cardData != null) {
                            cardData.setCardMask(cardMask);
                            cardData.setCardId(cardId);
                            cardData.setCardIssDate(cardIssDate);
                            cardData.setCardStartDate(cardStartDate);
                            cardData.setExpirationDate(expirationDate);
                            cardData.setInstanceId(instanceId);
                            cardData.setPrecedingInstanceId(precedingInstanceId);
                            cardData.setSequentialNumber(sequentialNumber);
                            cardData.setCardStatus(cardStatus);
                            cardData.setCardState(cardState);
                            cardData.setCategory(category);
                            cardData.setPvv(pvv);
                            cardData.setPinOffset(pinOffset);
                            cardData.setPinUpdateFlag(pinUpdateFlag);
                            cardData.setCardTypeId(cardTypeId);
                            cardData.setPrevCardNumber(prevCardNumber);
                            cardData.setPrevCardId(prevCardId);
                            cardData.setAgentNumber(agentNumber);
                            cardData.setAgentName(agentName);
                            cardData.setProductNumber(productNumber);
                            cardData.setProductName(productName);
                            cardData.setCompanyName(companyName);
                            cardData.setServiceCode(serviceCode);

                            cardData = cardRepository.save(cardData);
                        } else {

                            cardData = new Card();
                            cardData.setCardNumber(cardNumber);
                            cardData.setCardMask(cardMask);
                            cardData.setCardId(cardId);
                            cardData.setCardIssDate(cardIssDate);
                            cardData.setCardStartDate(cardStartDate);
                            cardData.setExpirationDate(expirationDate);
                            cardData.setInstanceId(instanceId);
                            cardData.setPrecedingInstanceId(precedingInstanceId);
                            cardData.setSequentialNumber(sequentialNumber);
                            cardData.setCardStatus(cardStatus);
                            cardData.setCardState(cardState);
                            cardData.setCategory(category);
                            cardData.setPvv(pvv);
                            cardData.setPinOffset(pinOffset);
                            cardData.setPinUpdateFlag(pinUpdateFlag);
                            cardData.setCardTypeId(cardTypeId);
                            cardData.setPrevCardNumber(prevCardNumber);
                            cardData.setPrevCardId(prevCardId);
                            cardData.setAgentNumber(agentNumber);
                            cardData.setAgentName(agentName);
                            cardData.setProductNumber(productNumber);
                            cardData.setProductName(productName);
                            cardData.setCompanyName(companyName);
                            cardData.setServiceCode(serviceCode);

                            cardData.setCustomer(customerData);
                            //
                            cardData = cardRepository.save(cardData);
                        }


                        Set<Account> listAccount = new HashSet<>();
                        NodeList accountList = custElement.getElementsByTagName("accountList");
                        for (int account = 0; account < accountList.getLength(); account++) {
                            Node accountNode = accountList.item(account);
                            if (accountNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element accountElement = (Element) accountNode;
                                String accountNumber = getElementValue(accountElement, "accountNumber");
                                String currency = getElementValue(accountElement, "currency");
                                String accountType = getElementValue(accountElement, "accountType");
                                String accountStatus = getElementValue(accountElement, "accountStatus");

                                List<Account> existingDataAccountInArray = listAccount.stream().filter(existData -> Objects.equals(existData.getAccountNumber(), accountNumber)).collect(Collectors.toList());
                                if (existingDataAccountInArray.size() > 0) {
                                    throw new Exception(String.format("Account Number %s duplicate detected", accountNumber));
                                }

                                Account accountData = accountRepository.findByAccountNumber(accountNumber);
                                if (accountData != null) {
                                    accountData.setCurrency(currency);
                                    accountData.setAccountType(accountType);
                                    accountData.setAccountStatus(accountStatus);
                                    accountRepository.save(accountData);
                                } else {
                                    accountData = new Account();
                                    accountData.setAccountNumber(accountNumber);
                                    accountData.setCurrency(currency);
                                    accountData.setAccountType(accountType);
                                    accountData.setAccountStatus(accountStatus);
                                    accountData.setCard(cardData);
                                    //
                                    accountRepository.save(accountData);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String getElementValue(Element parentElement, String tagName) {
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Element element = (Element) nodeList.item(0);
            return element.getTextContent();
        } else {
            return "Error";
        }
    }


    @Override
    public Customer findByCustomerId(String custNumber) {
        return repository.findByCustNumber(custNumber);
    }
}
