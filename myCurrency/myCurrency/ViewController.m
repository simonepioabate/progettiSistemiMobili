//
//  ViewController.m
//  myCurrency
//
//  Created by Simone Abate on 29/02/24.
//

#import "ViewController.h"
#import "CountryCurrencyDictionaryManager.h"
#import "CurrencyConverter.h"
#import "ApiFastForexManager.h"


@interface ViewController ()

@property (weak, nonatomic) IBOutlet UITextField *tfCurrencyValue;
@property (weak, nonatomic) IBOutlet UILabel *lblconvertedValue;
@property (weak, nonatomic) IBOutlet UIButton *fromCurrencyBtn;
@property (weak, nonatomic) IBOutlet UIButton *toCurrencyBtn;

@property (strong, nonatomic) CountryCurrencyDictionaryManager *countryCurrencyDictionaryManager;
@property (nonatomic, strong) NSDictionary *countryCurrencies;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    //imposta di default la prima valuta a 1
    self.tfCurrencyValue.text = @"1";

    // Imposta il ridimensionamento del testo per l'etichetta lblconvertedValue
    self.lblconvertedValue.adjustsFontSizeToFitWidth = YES;
    
    // Imposta le valute di partenza e destinazione predefinite
    if(self.currencyConverter == nil)
    {
        self.currencyConverter = [[CurrencyConverter alloc] init];
    }
    
    // Inizializza il dizionario countryCurrencies con le coppie valuta-paese
    self.countryCurrencyDictionaryManager = [[CountryCurrencyDictionaryManager alloc] init];
    self.countryCurrencies = self.countryCurrencyDictionaryManager.countryCurrencyDictionary;
    
    // setta i bottoni
    [ self.fromCurrencyBtn setTitle:self.currencyConverter.fromCurrencyTitle forState:UIControlStateNormal];
    [ self.toCurrencyBtn setTitle:self.currencyConverter.toCurrencyTitle forState:UIControlStateNormal];
    
    [self callConvertAPI];
    
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap:)]; //quando c'è un tocco si svolege il metodo handleTap
    tapGesture.cancelsTouchesInView = NO; // consento tocchi per raggiungere le altre viste
    [self.view addGestureRecognizer:tapGesture]; //aggiungo il gestore alla view
}

-(void)callConvertAPI {
    self.currencyConverter.selectedFromCurrencyValue = self.tfCurrencyValue.text;
    [ApiFastForexManager callConvertAPI:self.currencyConverter completion:^(NSString *value) {
        if (value) {
            // Aggiorna l'etichetta lblconvertedValue con il risultato della conversione
            self.currencyConverter.selectedToCurrencyValue = [NSString stringWithFormat: @"%@", value];
            // Aggiorna il testo della label solo dopo aver ricevuto il risultato
            //agggiungo  alla coda della richiesta il cambio del valore della label, in modo tale da eseguirla solo al termine
            dispatch_async(dispatch_get_main_queue(), ^{
                self.lblconvertedValue.text = self.currencyConverter.selectedToCurrencyValue;
            });
        } else {
            dispatch_async(dispatch_get_main_queue(), ^{
                self.lblconvertedValue.text = @"ERROR";
            });
        }
    }];

}

- (void)handleTap:(UITapGestureRecognizer *)gesture {
    // Ottiene le coordinate del tocco all'interno della vista
    CGPoint tapLocation = [gesture locationInView:self.view];

    // controlla se la tastiera non è attiva per il campo di testo tfCurrencyValue o se il tocco è al di fuori di esso
    // il metodo isFirstResponder ti dice se tfCurrencyValue ha il focus attivo
    if (![self.tfCurrencyValue isFirstResponder] || CGRectContainsPoint(self.tfCurrencyValue.frame, tapLocation) == NO) {
        [self.view endEditing:YES]; // Nasconde la tastiera se attualmente attiva
        [self callConvertAPI];
    }
}

- (IBAction)onClickDropDownFromCurrency:(UIButton *)sender {
    // In questo metodo, viene richiamato il metodo showDropDown per visualizzare il menu a discesa.
    [self showDropDown:sender];
}

- (IBAction)onClickDropDownToCurrency:(UIButton *)sender {
    // In questo metodo, viene richiamato il metodo showDropDownToCurrency per visualizzare il menu a discesa delle valute di partenza.
    [self showDropDown:sender];
}

- (void)showDropDown:(UIButton *)sender {
    //TODO: da capire bene
    // Crea un controller di avviso per visualizzare il menu a discesa
    UIAlertController *actionSheet = [UIAlertController alertControllerWithTitle:@"Choose Currency" message:nil preferredStyle:UIAlertControllerStyleActionSheet];

    // Itera attraverso il dizionario countryCurrencies
    for (NSString *currency in self.countryCurrencies) {
        NSString *flag = self.countryCurrencies[currency];
        NSString *title = [NSString stringWithFormat:@"%@: %@", flag, currency]; // Crea il titolo per ogni opzione del menu

        // Crea un'azione per l'opzione corrente del menu
        UIAlertAction *action = [UIAlertAction actionWithTitle:title style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
            //controllo chi è il bottone in questione per comportarmi di conseguenza
            if(sender == self.fromCurrencyBtn){
                self.currencyConverter.selectedFromCurrencyName = currency;
                self.currencyConverter.selectedFromCurrencyFlag = flag;
                [self.fromCurrencyBtn setTitle:title forState:UIControlStateNormal];
            }else{
                self.currencyConverter.selectedToCurrencyName = currency;
                self.currencyConverter.selectedToCurrencyFlag = flag;
                [self.toCurrencyBtn setTitle:title forState:UIControlStateNormal];
            }
            [self callConvertAPI];
        }];
        // Aggiunge l'azione all'elenco delle azioni del menu a discesa
        [actionSheet addAction:action];
    }

    // Aggiunge un'azione di cancellazione al menu a discesa
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:nil];
    [actionSheet addAction:cancelAction];

    // Presenta il menu a discesa
    [self presentViewController:actionSheet animated:YES completion:nil];
}
    
- (IBAction)onClickSendHistoryButton:(id)sender {
    UIStoryboard *mainStoryboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];

    // Istanzia il view controller dallo storyboard con l'identificatore "currencyVw"
    ViewController *currencyViewController = (ViewController*)[mainStoryboard instantiateViewControllerWithIdentifier:@"currencyHistoryView"];

    currencyViewController.currencyConverter = self.currencyConverter;
    // Push del nuovo viewController sulla navigation stack
    [self.navigationController pushViewController:currencyViewController animated:YES];
}

- (IBAction)fvBtn:(id)sender {
    
    NSArray *retrievedData = [[NSUserDefaults standardUserDefaults] objectForKey:@"currencyFavorite"];
    NSMutableArray *mutableArray;

    if ([retrievedData isKindOfClass:[NSArray class]]) {
        mutableArray = [retrievedData mutableCopy];
    } else {
        mutableArray = [NSMutableArray array];
    }
    
    Boolean notExistPair = YES;
    // Controllo su tutte le coppie se quella che voglio aggiungere esiste già
    for (NSDictionary *pair in mutableArray) {
        // Ora 'pair' contiene ciascuna coppia di dizionari all'interno dell'array
        
        // Estrarre le chiavi e i valori dalla coppia di dizionari
        NSString *fromCurrency = pair.allKeys.firstObject;
        NSString *toCurrency =pair.allValues.firstObject;
        
        if([self.currencyConverter.fromCurrencyTitle isEqualToString: fromCurrency] &&
            [self.currencyConverter.toCurrencyTitle isEqualToString: toCurrency])
        {
            notExistPair = NO;
        }
    }
    
    if(notExistPair){
        NSDictionary *newPair = @{self.currencyConverter.fromCurrencyTitle : self.currencyConverter.toCurrencyTitle};
        [mutableArray addObject:newPair];

        [[NSUserDefaults standardUserDefaults] setObject:mutableArray forKey:@"currencyFavorite"];
        [[NSUserDefaults standardUserDefaults] synchronize];

        [ self createAlert:@"Success" WithMessage:@"Add to Favourite."];

    } else{
        [ self createAlert:@"Failed" WithMessage:@"The pair already exists!" ];
    }
    
}


-(void) createAlert:(NSString *)title WithMessage:(NSString *)message{
    
    // Mostra un'alert per indicare che l'aggiunta ai preferiti è avvenuta con successo
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:title
                                                                             message:message
                                                                             preferredStyle:UIAlertControllerStyleAlert];

    // Crea un'azione per l'alert (ad esempio, il pulsante OK)
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:@"OK"
                                             style:UIAlertActionStyleDefault
                                             handler:^(UIAlertAction * _Nonnull action) {}];

    // Aggiunge l'azione al controller dell'alert
    [alertController addAction:okAction];

    // Presenta l'alert
    [self presentViewController:alertController animated:YES completion:nil];
    
}
@end
