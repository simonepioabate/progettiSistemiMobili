//
//  ViewController.h
//  myCurrency
//
//  Created by Simone Abate on 29/02/24.
//

#import <UIKit/UIKit.h>
#import "CurrencyConverter.h"
#import "CurrencyViewController.h"

@interface ViewController : UIViewController

@property (strong, nonatomic) CurrencyConverter *currencyConverter;

@end