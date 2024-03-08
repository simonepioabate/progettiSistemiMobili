//
//  ApiFastForexManager.h
//  myCurrency
//
//  Created by Simone Abate on 29/02/24.
//

#import <Foundation/Foundation.h>
#import "CurrencyConverter.h"
#import "DateManager.h"

NS_ASSUME_NONNULL_BEGIN

@interface ApiFastForexManager : NSObject

+ (void)callConvertAPI:(CurrencyConverter *)currencyConverter completion:(void (^)(NSString *))callback;

+ (void)getHistory:(CurrencyConverter *)currencyConverter date:(DateManager *)date completion:(void (^)(NSArray *))callback;

@end

NS_ASSUME_NONNULL_END
