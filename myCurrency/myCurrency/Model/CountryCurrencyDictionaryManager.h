//
//  CountryCurrencyDictionaryManager.h
//  myCurrency
//
//  Created by Simone Abate on 29/02/24.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface CountryCurrencyDictionaryManager : NSObject

- (instancetype)initWithValue:(NSDictionary *)dictionary;
- (instancetype)init;

@property (readonly, strong, nonatomic) NSDictionary *countryCurrencyDictionary;

@end

NS_ASSUME_NONNULL_END
